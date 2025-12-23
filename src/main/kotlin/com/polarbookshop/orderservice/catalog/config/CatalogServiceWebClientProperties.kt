package com.polarbookshop.orderservice.catalog.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI
import java.time.Duration

@ConfigurationProperties(prefix = "polar.catalog-service.webclient")
data class CatalogServiceWebClientProperties(
	val serviceUri: URI,

	val connectTimeout: Duration,
	val readTimeout: Duration,

	val backOffAttempts: Long,
	val minBackOff: Duration,
	val jitterFactor: Double,

	val booksRootApi: String = "/books",
	val getBookByIsbnUri: String = "$booksRootApi/{isbn}"
)
