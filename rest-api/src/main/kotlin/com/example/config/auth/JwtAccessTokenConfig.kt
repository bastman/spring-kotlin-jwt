package com.example.config.auth

object JwtAccessTokenConfig {
    const val CLAIM_ROLES = "https://awesome-app.example-company.com/claims/roles"

    const val CLAIM_USER_ID = "https://example-company.com/claims/userid"
    const val CLAIM_EMAIL = "https://example-company.com/claims/email"
    const val CLAIM_GIVEN_NAME = "https://example-company.com/claims/given_name"
    const val CLAIM_FAMILY_NAME = "https://example-company.com/claims/family_name"
}