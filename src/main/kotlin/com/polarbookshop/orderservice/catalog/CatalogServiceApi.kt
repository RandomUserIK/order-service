package com.polarbookshop.orderservice.catalog

import com.polarbookshop.orderservice.catalog.model.Book
import reactor.core.publisher.Mono

interface CatalogServiceApi {
	fun getBookByIsbn(isbn: String): Mono<Book>
}
