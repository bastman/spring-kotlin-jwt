package com.example.api

import com.example.api.handler.ping.PingResponse
import com.example.api.handler.user.MeHandler
import com.example.api.handler.user.MeResponse
import mu.KLogging
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
class UserApiController(
        private val meHandler: MeHandler
) {

    @GetMapping("/api/ping")
    fun ping(): PingResponse = PingResponse(now = Instant.now())

    @GetMapping("/api/me")
    fun me(authentication: Authentication?): MeResponse = meHandler.handle(authentication)

    companion object : KLogging()
}


