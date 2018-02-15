package com.example.util.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.Jwts
import java.time.Duration
import java.time.Instant
import java.util.*


fun JwtBuilder.expireAt(value: Instant): JwtBuilder =
        setExpiration(Date.from(value))

fun JwtBuilder.expireIn(value: Duration, now: Instant = Instant.now()): JwtBuilder =
        expireAt(now + value)

fun JwtBuilder.issuedAt(value: Instant): JwtBuilder =
        setIssuedAt(Date.from(value))

fun jwtBuilder(claims: Map<String, Any?>): JwtBuilder = Jwts.builder().setClaims(claims)
fun jwtBuilder(claims: Claims): JwtBuilder = Jwts.builder().setClaims(claims)
