package com.example.api.auth

data class ApiUser(val userDetails: ApiUserDetails, val authorities: List<String>)
data class ApiUserDetails(val userId: String, val email: String, val givenName: String, val familyName: String) {
    companion object {
        val EMPTY = ApiUserDetails(userId = "", email = "", givenName = "", familyName = "")
    }
}