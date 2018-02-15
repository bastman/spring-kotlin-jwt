package com.example.config.auth.jwtfake

import com.auth0.spring.security.api.JwtWebSecurityConfigurer
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.stereotype.Component

@Component
class JwtFakeWebSecurity(
        @Value(value = "\${app.auth.auth0.apiAudience}")
        private val apiAudience: String,
        @Value(value = "\${app.auth.auth0.issuer}")
        private val issuer: String
) {

    fun configureHttpSecurity(http: HttpSecurity) {
        JwtWebSecurityConfigurer
                .forRS256(
                        apiAudience, issuer
                )
                .configure(http)
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/ping").authenticated()
                .anyRequest().authenticated()
        logger.info { "configured web-security. (apiAudience=$apiAudience issuer=$issuer" }
    }

    companion object : KLogging()
}