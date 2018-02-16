package com.example.api

import com.auth0.jwt.interfaces.DecodedJWT
import com.example.config.auth.*
import com.example.util.auth0.CustomAuthenticationJsonWebToken
import com.example.util.auth0.scopes
import mu.KLogging
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
class PingController {
    @GetMapping("/api/ping")
    fun ping(authentication: Authentication?): PingResponse {
        val me = when (authentication) {
            is CustomAuthenticationJsonWebToken -> authentication.details.toMe()
            else -> null
        }

        val authorities = if (authentication != null) {
            authentication.authorities.map { it.toString() }
        } else emptyList()

        logger.info { "AUTH: $authentication" }

        return PingResponse(now = Instant.now(), me = me, authorities = authorities)
    }

    companion object : KLogging()
}

data class PingResponse(val now: Instant, val me: Me?, val authorities: List<String>)
data class Me(
        val userId: String,
        val email: String,
        val givenName: String,
        val familyName: String,
        val roles: List<String>,
        val scopes: List<String>
)

private fun DecodedJWT.toMe() = Me(
        userId = userId() ?: "",
        email = email() ?: "",
        givenName = givenName() ?: "",
        familyName = familyName() ?: "",
        roles = roles(),
        scopes = scopes()
)