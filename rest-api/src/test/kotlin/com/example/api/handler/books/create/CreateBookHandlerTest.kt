package com.example.api.handler.books.create

import com.example.api.auth.ApiUser
import com.example.api.auth.ApiUserDetails
import com.example.domain.bookstore.BookRepo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CreateBookHandlerTest(
        @Autowired val handler: CreateBookHandler,
        @Autowired val bookRepo: BookRepo
) {

    private val API_USER_EMPTY = ApiUser(userDetails = ApiUserDetails.EMPTY, authorities = emptyList())

    @Test
    fun `context loads`() {

    }

    @Test
    fun `foo`() {
        val req = CreateBookRequest(title = "Book A")
        val user = API_USER_EMPTY.copy(authorities = listOf("AUTHORITY_A"))
        val resp= handler.handle(req=req, apiUser = user)

        println(resp)
    }
}