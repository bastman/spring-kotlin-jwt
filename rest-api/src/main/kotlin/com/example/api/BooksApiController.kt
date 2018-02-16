package com.example.api

import com.example.api.auth.ApiUser
import com.example.api.auth.ApiUserService
import com.example.api.handler.books.create.CreateBookHandler
import com.example.api.handler.books.create.CreateBookRequest
import com.example.api.handler.books.create.CreateBookResponse
import mu.KLogging
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class BooksApiController(
        private val apiUserService: ApiUserService,
        private val createBookHandler: CreateBookHandler
) {

    @PostMapping("/api/books")
    fun createBook(@RequestBody req: CreateBookRequest, auth: Authentication): CreateBookResponse =
            createBookHandler.handle(req = req, apiUser = apiUser(auth))

    private fun apiUser(auth: Authentication): ApiUser = apiUserService.apiUserFromAuthentication(auth)

    companion object : KLogging()
}
