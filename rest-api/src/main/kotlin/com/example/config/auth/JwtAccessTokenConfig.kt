package com.example.config.auth

import com.auth0.jwt.interfaces.DecodedJWT
import com.example.config.auth.JwtAccessTokenConfig.CLAIM_EMAIL
import com.example.config.auth.JwtAccessTokenConfig.CLAIM_FAMILY_NAME
import com.example.config.auth.JwtAccessTokenConfig.CLAIM_GIVEN_NAME
import com.example.config.auth.JwtAccessTokenConfig.CLAIM_ROLES
import com.example.config.auth.JwtAccessTokenConfig.CLAIM_USER_ID
import com.example.util.auth0.claimAsListOfString
import com.example.util.auth0.claimAsString

object JwtAccessTokenConfig {
    const val CLAIM_ROLES = "https://awesome-app.example-company.com/claims/roles"

    const val CLAIM_USER_ID = "https://example-company.com/claims/userid"
    const val CLAIM_EMAIL = "https://example-company.com/claims/email"
    const val CLAIM_GIVEN_NAME = "https://example-company.com/claims/given_name"
    const val CLAIM_FAMILY_NAME = "https://example-company.com/claims/family_name"
}

fun DecodedJWT.userId() = this.claimAsString(CLAIM_USER_ID)
fun DecodedJWT.email() = this.claimAsString(CLAIM_EMAIL)
fun DecodedJWT.givenName() = this.claimAsString(CLAIM_GIVEN_NAME)
fun DecodedJWT.familyName() = this.claimAsString(CLAIM_FAMILY_NAME)
fun DecodedJWT.roles() = this.claimAsListOfString(CLAIM_ROLES)