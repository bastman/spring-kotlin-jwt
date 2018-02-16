package com.example.api.auth

import com.auth0.jwt.interfaces.DecodedJWT
import com.example.config.auth.email
import com.example.config.auth.familyName
import com.example.config.auth.givenName
import com.example.config.auth.userId
import com.example.util.auth0.CustomAuthenticationJsonWebToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class ApiUserService {
    fun apiUserFromAuthentication(authentication: Authentication): ApiUser {
        val userDetails = when (authentication) {
            is CustomAuthenticationJsonWebToken -> authentication.details.toApiUserDetails()
            else -> ApiUserDetails.EMPTY
        }

        return ApiUser(userDetails = userDetails, authorities = authentication.authorityNames())
    }
}

private fun Authentication.authorityNames(): List<String> = authorities.toList().map { it.authority }
private fun DecodedJWT.toApiUserDetails(): ApiUserDetails =
        ApiUserDetails(
                userId = userId() ?: "",
                email = email() ?: "",
                givenName = givenName() ?: "",
                familyName = familyName() ?: ""
        )
