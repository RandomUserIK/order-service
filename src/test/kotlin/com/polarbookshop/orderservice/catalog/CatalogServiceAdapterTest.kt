package com.polarbookshop.orderservice.catalog

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpHeaders
import com.polarbookshop.orderservice.catalog.config.CatalogServiceAdapter
import com.polarbookshop.orderservice.catalog.config.CatalogServiceWebClientProperties
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier
import java.time.Duration

internal class CatalogServiceAdapterTest {
	private val webMockServer = MockWebServer()
	private val webClient = WebClient
		.builder()
		.baseUrl(webMockServer.url("/").toUri().toString())
		.build()
	private val webClientProperties = CatalogServiceWebClientProperties(
		serviceUri = webMockServer.url("/").toUri(),
		connectTimeout = Duration.ofSeconds(3),
		readTimeout = Duration.ofSeconds(2),
		backOffAttempts = 3L,
		minBackOff = Duration.ofMillis(100L),
		jitterFactor = 0.5,
	)
	private val catalogServiceAdapter = CatalogServiceAdapter(webClient, webClientProperties)

	@BeforeEach
	fun setUp() {
		webMockServer.start()
	}

	@AfterEach
	fun tearDown() {
		webMockServer.shutdown()
	}

	@Test
	fun whenBookExistsThenReturnBook() {
		// GIVEN
		val bookIsbn = "1234567890"
		val mockResponse = MockResponse()
			.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.setBody(
				"""
				{
				"isbn": "$bookIsbn",
				"title": "Title",
				"author": "Author",
				"price": 9.90,
				"publisher": "Polarsophia"
				}
			""".trimIndent(),
			)

		webMockServer.enqueue(mockResponse)

		// WHEN
		val response = catalogServiceAdapter.getBookByIsbn(bookIsbn)

		// THEN
		StepVerifier
			.create(response)
			.expectNextMatches {
				it.isbn == bookIsbn
			}
			.verifyComplete()
	}
}
