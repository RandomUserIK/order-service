package com.polarbookshop.orderservice.catalog.config

import com.polarbookshop.orderservice.catalog.CatalogServiceApi
import com.polarbookshop.orderservice.catalog.model.Book
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import reactor.util.retry.Retry

class CatalogServiceAdapter(
	private val webClient: WebClient,
	private val webClientProperties: CatalogServiceWebClientProperties,
) : CatalogServiceApi {

	override fun getBookByIsbn(isbn: String): Mono<Book> =
		webClient
			.get()
			.uri { it.path(webClientProperties.getBookByIsbnUri).build(isbn) }
			.retrieve()
			.bodyToMono<Book>()
			.timeout(webClientProperties.readTimeout, Mono.empty())
			.onErrorResume(WebClientResponseException.NotFound::class.java) { Mono.empty() }
			.retryWhen(
				Retry
					.backoff(
						webClientProperties.backOffAttempts,
						webClientProperties.minBackOff,
					)
					.jitter(webClientProperties.jitterFactor)
			)
			.onErrorResume(Exception::class.java) { Mono.empty() }
}
