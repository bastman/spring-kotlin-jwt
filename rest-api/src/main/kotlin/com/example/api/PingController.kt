package com.example.api

import com.example.api.auth.ApiUser
import com.example.api.auth.ApiUserDetails
import com.example.api.auth.ApiUserService
import com.example.config.auth.roles
import com.example.util.auth0.CustomAuthenticationJsonWebToken
import com.example.util.auth0.scopes
import mu.KLogging
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
class PingController(private val apiUserService: ApiUserService) {

    @GetMapping("/api/ping")
    fun ping(authentication: Authentication?): PingResponse {
        val apiUser: ApiUser? = authentication?.toApiUser(apiUserService)
        logger.info { "auth=$authentication apiUser=$apiUser" }

        return PingResponse(
                now = Instant.now(),
                user = apiUser?.userDetails,
                auth = ApiUserAuthDto(
                        authorities = apiUser?.authorities ?: emptyList(),
                        scopes = authentication?.scopes() ?: emptyList(),
                        roles = authentication?.roles() ?: emptyList()
                )
        )
    }

    companion object : KLogging()
}

data class PingResponse(val now: Instant, val user: ApiUserDetails?, val auth: ApiUserAuthDto)
data class ApiUserAuthDto(val authorities: List<String>, val roles: List<String>, val scopes: List<String>)

private fun Authentication.toApiUser(apiUserService: ApiUserService): ApiUser =
        apiUserService.apiUserFromAuthentication(this)

private fun Authentication.roles(): List<String> =
        when (this) {
            is CustomAuthenticationJsonWebToken -> details.roles()
            else -> emptyList()
        }

private fun Authentication.scopes(): List<String> =
        when (this) {
            is CustomAuthenticationJsonWebToken -> details.scopes()
            else -> emptyList()
        }
