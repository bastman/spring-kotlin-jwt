package com.example.config.auth.auth0

import com.auth0.jwt.interfaces.DecodedJWT
import com.example.config.auth.authorities
import com.example.util.auth0.CustomJwtWebSecurityConfigurer
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.stereotype.Component

@Component
class Auth0WebSecurity(
        @Value(value = "\${app.auth.auth0.apiAudience}")
        private val apiAudience: String,
        @Value(value = "\${app.auth.auth0.issuer}")
        private val issuer: String
) {

    fun configureHttpSecurity(http: HttpSecurity) {
        CustomJwtWebSecurityConfigurer
                .forRS256(audience = apiAudience, issuer = issuer)
                .copy(authoritiesConverter = { authoritiesFromJwtDecoded(it) })
                .configure(http)
                .authorizeRequests()
                .anyRequest().authenticated()
        logger.info { "configured web-security. (apiAudience=$apiAudience issuer=$issuer" }
    }

    private fun authoritiesFromJwtDecoded(it: DecodedJWT) = it.authorities()

    companion object : KLogging()
}