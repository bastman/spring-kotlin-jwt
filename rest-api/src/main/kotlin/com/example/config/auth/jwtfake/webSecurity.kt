package com.example.config.auth.jwtfake

import com.auth0.jwt.interfaces.DecodedJWT
import com.example.config.auth.roles
import com.example.util.auth0.CustomJwtWebSecurityConfigurer
import com.example.util.auth0.scopes
import com.example.util.jwt.expireIn
import com.example.util.jwt.jwtBuilder
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.SignatureAlgorithm
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*
import javax.servlet.ServletRequest

@Component
class JwtFakeWebSecurity(
        @Value(value = "\${app.auth.auth0.apiAudience}")
        private val apiAudience: String,
        @Value(value = "\${app.auth.auth0.issuer}")
        private val issuer: String
) {

    fun configureHttpSecurity(http: HttpSecurity) {
        CustomJwtWebSecurityConfigurer
                .forHS256WithBase64Secret(audience = apiAudience, issuer = issuer, secretB64 = JWT_SECRET_B64)
                .copy(authoritiesConverter = { authoritiesFromJwtDecoded(it) })
                .copy(tokenProvider = { tokenFromRequest(it) })
                .configure(http)
                .authorizeRequests()
                .and()
                .antMatcher("/api/**")
                .authorizeRequests().anyRequest().authenticated()
    }

    private fun authoritiesFromJwtDecoded(it: DecodedJWT) = it.roles() + it.scopes()

    private fun tokenFromRequest(request: ServletRequest): String? {
        val json = loadResource("/jwt/fake/full-access.json")
        val claims: Map<String, Any?> = jacksonObjectMapper().readValue(json)
        val jwtBuilder = jwtBuilder(claims)
        val tokenText = jwtBuilder.expireIn(Duration.ofDays(3))
                .signWith(SignatureAlgorithm.HS256, JwtFakeWebSecurity.JWT_SECRET_B64)
                .compact()

        return tokenText
    }


    companion object : KLogging() {
        val JWT_SECRET: String = "mysecret"
        val JWT_SECRET_B64: String = Base64.getEncoder().encodeToString(JWT_SECRET.toByteArray(charset = Charsets.UTF_8))
    }
}


fun loadResource(resource: String): String =
        try {
            object {}.javaClass.getResource(resource)
                    .readText(Charsets.UTF_8)
        } catch (all: Exception) {
            throw RuntimeException("Failed to load resource=$resource!", all)
        }