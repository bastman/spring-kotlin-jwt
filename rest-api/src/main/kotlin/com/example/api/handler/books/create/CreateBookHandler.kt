package com.example.api.handler.books.create

import com.example.api.auth.ApiAuthErrorType
import com.example.api.auth.ApiAuthException
import com.example.api.auth.ApiUser
import com.example.domain.bookstore.Book
import com.example.domain.bookstore.BookRepo
import mu.KLogging
import org.funktionale.tries.Try
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class CreateBookHandler(private val bookRepo: BookRepo) {

    fun handle(req: CreateBookRequest, apiUser: ApiUser): CreateBookResponse {
        val permissionRequired = "AUTHORITY_A"

        simpleSpec("User has permission: $permissionRequired") {
            "a user exists" {}
            "a user has auth" {
                apiUser.hasAuthority(authority = permissionRequired)
            }
        }
        /*
        check(apiUser.hasAuthority(permissionRequired))


        Try {  requireThat {
            "User has permission: $permissionRequired" using apiUser.hasAuthority(permissionRequired)
        }}.onFailure {
            logger.error { "ERROR: ${it.message}" }
            throw it
        }



        apiUser.requireAuthority(authority = permissionRequired)
        */
        val book = Book(id = UUID.randomUUID(), modifiedAt = Instant.now(), title = req.title)
        bookRepo.put(book)
        return CreateBookResponse(book = book)
    }
    companion object:KLogging()
}

fun ApiUser.hasAuthority(authority: String) = authority in authorities
fun ApiUser.requireAuthority(authority: String) {
    if (!hasAuthority(authority)) {
        throw  ApiAuthException(type = ApiAuthErrorType.PERMISSION_DENIED, message = "requires authority=$authority")
    }
}

object Requirements {
    @Suppress("NOTHING_TO_INLINE")
    inline infix fun String.using(expr: Boolean) {
        if (!expr) throw IllegalArgumentException("Failed requirement: $this")
    }


}

inline fun <R> requireThat(body: Requirements.() -> R) = Requirements.body()
inline fun <R> checkThat(body: Requirements.() -> R) = Requirements.body()

class SimpleSpec(private val name: String = "") {
    private val steps: MutableList<String> = mutableListOf()

    fun getName(): String = name
    fun getSteps(): List<String> = steps.toList()

    operator fun <T> String.invoke(block: SimpleSpec.() -> T): T {
        steps += this
        return block()
    }
}

fun simpleSpec(name: String = "", block: SimpleSpec.() -> Unit) {

    val specName: String = if (name.isNotBlank()) {
        name
    } else {
        val t = Throwable()
        val trace = t.stackTrace
        val caller = trace.find {
            (!it.isNativeMethod) && (!it.methodName.contains("simpleSpec"))
        }
        when (caller) {
            null -> "$block"
            else -> "${caller.className}.${caller.methodName}"
        }
    }

    val spec = SimpleSpec(name = specName)
    try {
        spec.apply(block)
    }catch (all:Exception) {
        val msg ="Spec Failed! ${spec.getName()} steps: ${spec.getSteps().joinToString(" - ")} details: ${all.message}"

        all.printStackTrace()
        throw IllegalStateException(msg, all)
    }

}