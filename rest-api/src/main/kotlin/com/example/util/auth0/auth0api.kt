package com.example.util.auth0

import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.spring.security.api.JwtAuthenticationEntryPoint
import com.auth0.spring.security.api.JwtAuthenticationProvider
import com.auth0.spring.security.api.authentication.JwtAuthentication
import org.apache.commons.codec.binary.Base64
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpRequestResponseHolder
import org.springframework.security.web.context.SecurityContextRepository
import java.time.Instant
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

typealias JwtAuthoritiesConverter = (decodedJWT: DecodedJWT) -> List<String>
typealias TokenFromRequestProvider = (request: HttpServletRequest) -> String?

data class CustomJwtWebSecurityConfigurer(
        private val audience: String,
        private val issuer: String,
        private val provider: AuthenticationProvider,
        private val authoritiesConverter: JwtAuthoritiesConverter = AUTHORITIES_CONVERTER_AUTH0_DEFAULT,
        private val tokenProvider: TokenFromRequestProvider = TOKEN_FROM_REQUEST_PROVIDER_AUTH0_DEFAULT
) {
    @Throws(Exception::class)
    fun configure(http: HttpSecurity): HttpSecurity =
            http
                    .authenticationProvider(provider)
                    .securityContext()
                    .securityContextRepository(
                            CustomBearerSecurityContextRepository(
                                    authoritiesConverter = authoritiesConverter,
                                    tokenFromRequestProvider = tokenProvider
                            )
                    )
                    .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(JwtAuthenticationEntryPoint())
                    .and()
                    .httpBasic().disable()
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

    companion object {
        val AUTHORITIES_CONVERTER_AUTH0_DEFAULT: JwtAuthoritiesConverter = { it.scopes(claimName = "scope") }
        val TOKEN_FROM_REQUEST_PROVIDER_AUTH0_DEFAULT: TokenFromRequestProvider = { tokenFromRequest(it) }

        private fun tokenFromRequest(request: HttpServletRequest): String? {
            val value = request.getHeader("Authorization")
            if (value == null || !value.toLowerCase().startsWith("bearer")) {
                return null
            }
            val parts = value.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return if (parts.size < 2) {
                null
            } else parts[1].trim { it <= ' ' }
        }


        fun forRS256(
                audience: String, issuer: String, provider: AuthenticationProvider? = null
        ): CustomJwtWebSecurityConfigurer {
            val authProvider = if (provider == null) {
                val jwkProvider = JwkProviderBuilder(issuer).build()
                JwtAuthenticationProvider(jwkProvider, issuer, audience)
            } else {
                provider
            }
            return CustomJwtWebSecurityConfigurer(audience, issuer, authProvider)
        }

        fun forHS256WithBase64Secret(
                audience: String, issuer: String, secretB64: String
        ): CustomJwtWebSecurityConfigurer {
            val secretBytes = Base64(true).decode(secretB64)
            return CustomJwtWebSecurityConfigurer(
                    audience = audience, issuer = issuer,
                    provider = JwtAuthenticationProvider(secretBytes, issuer, audience)
            )
        }

        fun forHS256(
                audience: String, issuer: String, secret: ByteArray
        ): CustomJwtWebSecurityConfigurer =
                CustomJwtWebSecurityConfigurer(
                        audience = audience, issuer = issuer,
                        provider = JwtAuthenticationProvider(secret, issuer, audience)
                )

        fun forHS256(
                audience: String, issuer: String, provider: AuthenticationProvider
        ): CustomJwtWebSecurityConfigurer =
                CustomJwtWebSecurityConfigurer(audience, issuer, provider)
    }
}

class CustomBearerSecurityContextRepository(
        private val authoritiesConverter: JwtAuthoritiesConverter,
        private val tokenFromRequestProvider: TokenFromRequestProvider
) : SecurityContextRepository {
    override fun saveContext(context: SecurityContext, request: HttpServletRequest, response: HttpServletResponse) {}
    override fun containsContext(request: HttpServletRequest): Boolean = tokenFromRequest(request) != null
    override fun loadContext(requestResponseHolder: HttpRequestResponseHolder): SecurityContext {
        val context = SecurityContextHolder.createEmptyContext()
        val token = tokenFromRequest(requestResponseHolder.request)
        val authentication = CustomPreAuthenticatedAuthenticationJsonWebToken
                .usingToken(token = token, authoritiesConverter = authoritiesConverter)
        if (authentication != null) {
            context.authentication = authentication
            logger.debug("Found bearer token in request. Saving it in SecurityContext")
        }
        return context
    }

    private fun tokenFromRequest(request: HttpServletRequest): String? = tokenFromRequestProvider.invoke(request)

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
}


class CustomAuthenticationJsonWebToken @Throws(JWTVerificationException::class)
constructor(
        token: String,
        verifier: JWTVerifier?,
        private val authoritiesConverter: JwtAuthoritiesConverter
) : Authentication, JwtAuthentication {
    private val decoded: DecodedJWT = if (verifier == null) JWT.decode(token) else verifier.verify(token)
    private var authenticated: Boolean = verifier != null

    override fun getToken(): String = decoded.token
    override fun getKeyId(): String = decoded.keyId
    override fun getCredentials(): Any = decoded.token
    override fun getDetails(): DecodedJWT = decoded
    override fun getPrincipal(): Any = decoded.subject
    override fun isAuthenticated(): Boolean = authenticated
    override fun getName(): String = decoded.subject

    @Throws(JWTVerificationException::class)
    override fun verify(verifier: JWTVerifier): Authentication =
            CustomAuthenticationJsonWebToken(token, verifier, authoritiesConverter)

    @Throws(IllegalArgumentException::class)
    override fun setAuthenticated(isAuthenticated: Boolean) {
        if (isAuthenticated) {
            throw IllegalArgumentException("Must create a new instance to specify that the authentication is valid")
        }
        this.authenticated = false
    }

    override fun getAuthorities(): Collection<GrantedAuthority> =
            authoritiesConverter.invoke(decoded)
                    .map { SimpleGrantedAuthority(it) }
                    .toMutableList()
}


class CustomPreAuthenticatedAuthenticationJsonWebToken(
        private val token: DecodedJWT,
        private val authoritiesConverter: JwtAuthoritiesConverter
) : Authentication, JwtAuthentication {
    override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()
    override fun getCredentials(): Any = token.token
    override fun getDetails(): Any = token
    override fun getPrincipal(): Any = token.subject
    override fun isAuthenticated(): Boolean = false
    @Throws(IllegalArgumentException::class)
    override fun setAuthenticated(isAuthenticated: Boolean) {
    }

    override fun getName(): String = token.subject
    override fun getToken(): String = token.token
    override fun getKeyId(): String = token.keyId
    @Throws(JWTVerificationException::class)
    override fun verify(verifier: JWTVerifier): Authentication =
            CustomAuthenticationJsonWebToken(
                    token = token.token,
                    verifier = verifier,
                    authoritiesConverter = authoritiesConverter
            )

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        fun usingToken(
                token: String?,
                authoritiesConverter: JwtAuthoritiesConverter
        ): CustomPreAuthenticatedAuthenticationJsonWebToken? =
                if (token == null) {
                    logger.debug("No token was provided to build ${this::class.java.name}")
                    null
                } else {
                    try {
                        CustomPreAuthenticatedAuthenticationJsonWebToken(
                                token = JWT.decode(token),
                                authoritiesConverter = authoritiesConverter
                        )
                    } catch (e: JWTDecodeException) {
                        logger.debug("Failed to decode token as jwt", e)
                        null
                    }
                }
    }
}

fun DecodedJWT.claimAsString(name: String): String? {
    val claim = this.getClaim(name)
    return if (claim == null || claim.isNull) {
        null
    } else claim.asString()
}

fun DecodedJWT.expiresAt(): Instant? = this.expiresAt?.toInstant()
fun DecodedJWT.principal(): String = this.subject ?: ""
fun DecodedJWT.credentials(): String = this.token ?: ""
fun DecodedJWT.claimAsListOfString(claimName: String): List<String> {
    val claim = this.getClaim(claimName)

    return if (claim == null || claim.isNull) {
        emptyList()
    } else {
        claim.asList(String::class.java)
                .toList()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()
    }
}

fun DecodedJWT.scopes(claimName: String = "scope"): List<String> {
    val scope = claimAsString(claimName)
    if (scope == null || scope.trim { it <= ' ' }.isEmpty()) {
        return emptyList()
    }
    val scopes = scope.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
    return scopes.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
}