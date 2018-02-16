package com.example.api.auth

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.FORBIDDEN)
class ApiAuthException(val type: ApiAuthErrorType, message: String) : RuntimeException(message)

enum class ApiAuthErrorType { PERMISSION_DENIED; }