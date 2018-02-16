package com.example.api.handler.user

import com.example.config.Jackson
import com.fasterxml.jackson.module.kotlin.readValue
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("prod")
class MeHandlerMockMvcTest(@Autowired val mockMvc: MockMvc) {
    @Test
    fun `context loads`() {

    }

    @Test
    fun `GET request with no auth - should fail`() {
        mockMvc.perform(get("/api/me"))
                .andDo(print())
                .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(authorities = ["MOCKED_AUTHORITY_A", "MOCKED_AUTHORITY_B"])
    fun `GET request with mocked authorities - should work`() {
        val response = mockMvc.perform(get("/api/me"))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn().response

        val apiResponse: MeResponse = JSON.readValue(response.contentAsString)

        apiResponse.auth.authorities shouldEqual listOf("MOCKED_AUTHORITY_A", "MOCKED_AUTHORITY_B")
    }

    companion object {
        val JSON = Jackson.defaultMapper()
    }
}