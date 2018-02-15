package com.example.util.jwt

import io.jsonwebtoken.Jwts
import java.nio.charset.Charset
import java.util.*

object Jwt {
    fun parse(jwt: String) = jwt
            .removePrefix("Bearer").trim()
            .replaceAfterLast(".", "")
            .let { Jwts.parser().parse(it) }

    fun decodeBase64(source: String, charset: Charset = Charsets.UTF_8): String = base64Decoder.decode(source).toString(charset)
    fun encodeBase64(source: String, charset: Charset = Charsets.UTF_8): String = base64Encoder.encodeToString(source.toByteArray(charset))
    private val base64Decoder = Base64.getDecoder()
    private val base64Encoder = Base64.getEncoder()
}

data class PlaintextJwtB64(val delimiter: String, val header: String, val payload: String, val signature: String) {
    fun toText(): String = listOf(header, payload, signature).joinToString(delimiter)
    fun removeSignature() = copy(signature = "")

    companion object {
        fun ofBearer(jwt: String, delimiter: String = "."): PlaintextJwtB64 =
                ofText(jwt = removeBearerPrefix(jwt), delimiter = delimiter)

        fun ofText(jwt: String, delimiter: String = "."): PlaintextJwtB64 {
            val parts = jwt.trim().split(delimiter)
            return PlaintextJwtB64(
                    delimiter = delimiter,
                    header = parts.getOrNull(0) ?: "",
                    payload = parts.getOrNull(1) ?: "",
                    signature = parts.getOrNull(2) ?: ""
            )
        }

        private fun removeBearerPrefix(jwt: String) = jwt.removePrefix("Bearer").trim()
    }
}

data class PlaintextJwt(val delimiter: String, val header: String, val payload: String, val signature: String)

fun PlaintextJwtB64.decodeB64(): PlaintextJwt =
        PlaintextJwt(
                delimiter = delimiter,
                header = Jwt.decodeBase64(header),
                payload = Jwt.decodeBase64(payload),
                signature = signature
        )

fun PlaintextJwtB64.toBearer(): String = "Bearer ${toText()}"

fun PlaintextJwt.encodeB64(): PlaintextJwtB64 =
        PlaintextJwtB64(
                delimiter = delimiter,
                header = Jwt.encodeBase64(header),
                payload = Jwt.encodeBase64(payload),
                signature = signature
        )
