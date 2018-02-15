package com.example.config

import com.example.config.auth.ApiAuthStrategyName
import com.example.config.auth.auth0.Auth0WebSecurity
import com.example.config.auth.jwtfake.JwtFakeWebSecurity
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity(debug = true)
class WebSecurity(
        @Value(value = "\${app.auth.strategy}")
        private val authStrategyName: ApiAuthStrategyName,
        private val webSecurityAuth0: Auth0WebSecurity,
        private val webSecurityJwtFake: JwtFakeWebSecurity
) : WebSecurityConfigurerAdapter() {

    override fun configure(web: WebSecurity) {
        web.ignoring()
                .antMatchers(
                        // actuator
                        "/health",
                        "/info",
                        // swagger
                        "/v2/api-docs",
                        "/configuration/ui",
                        "/swagger-resources/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**"
                )
                .antMatchers(HttpMethod.OPTIONS, "/**");
    }

    override fun configure(http: HttpSecurity) =
            when (authStrategyName) {
                ApiAuthStrategyName.AUTH0 -> webSecurityAuth0.configureHttpSecurity(http)
                ApiAuthStrategyName.JWT_FAKE -> webSecurityJwtFake.configureHttpSecurity(http)
            }.also {
                logger.info { "WebSecurity configured (strategy=$authStrategyName)" }
            }

    companion object : KLogging()
}
