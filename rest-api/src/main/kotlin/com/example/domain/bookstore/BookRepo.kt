package com.example.domain.bookstore

import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class BookRepo {

    private val items: MutableMap<UUID, Book> = mutableMapOf()

    fun put(item: Book) {
        items[item.id] = item
    }

    fun get(id: UUID): Book? = items[id]

    fun delete(id: UUID) = items.remove(id)
}


data class Book(val id: UUID, val modifiedAt: Instant, val title: String)