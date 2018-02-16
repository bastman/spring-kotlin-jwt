package com.example.api.handler.user

import com.example.api.auth.ApiUserDetails

data class MeResponse(val user: ApiUserDetails?, val auth: ApiUserAuthDto)
data class ApiUserAuthDto(val authorities: List<String>, val roles: List<String>, val scopes: List<String>)