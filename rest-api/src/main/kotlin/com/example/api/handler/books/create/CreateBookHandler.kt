package com.example.api.handler.books.create

import com.example.api.auth.ApiAuthErrorType
import com.example.api.auth.ApiAuthException
import com.example.api.auth.ApiUser
import com.example.domain.bookstore.Book
import com.example.domain.bookstore.BookRepo
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class CreateBookHandler(private val bookRepo: BookRepo) {

    fun handle(req: CreateBookRequest, apiUser: ApiUser): CreateBookResponse {
        val permissionRequired = "AUTHORITY_A"
        apiUser.requireAuthority(authority = permissionRequired)
        val book = Book(id = UUID.randomUUID(), modifiedAt = Instant.now(), title = req.title)
        bookRepo.put(book)
        return CreateBookResponse(book = book)
    }
}

fun ApiUser.hasAuthority(authority: String) = authority in authorities
fun ApiUser.requireAuthority(authority: String) {
    if (!hasAuthority(authority)) {
        throw  ApiAuthException(type = ApiAuthErrorType.PERMISSION_DENIED, message = "requires authority=$authority")
    }
}