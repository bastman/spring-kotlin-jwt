package com.example.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
class PingController {
    @GetMapping("/api/ping")
    fun ping(): PingResponse = PingResponse(now = Instant.now())
}

data class PingResponse(val now: Instant)