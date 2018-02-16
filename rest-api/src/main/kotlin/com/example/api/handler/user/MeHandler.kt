package com.example.api.handler.user

import com.example.api.auth.ApiUser
import com.example.api.auth.ApiUserService
import com.example.config.auth.roles
import com.example.util.auth0.CustomAuthenticationJsonWebToken
import com.example.util.auth0.scopes
import mu.KLogging
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class MeHandler(private val apiUserService: ApiUserService) {

    fun handle(authentication: Authentication?): MeResponse {
        val apiUser: ApiUser? = authentication?.toApiUser(apiUserService)
        logger.info { "auth=$authentication apiUser=$apiUser" }

        return MeResponse(
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