package com.polarbookshop.orderservice.catalog.config

import io.netty.channel.ChannelOption
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CatalogServiceWebClientProperties::class)
class CatalogServiceWebClientConfiguration(
	private val webClientProperties: CatalogServiceWebClientProperties,
) {

	@Bean
	fun catalogServiceWebClient(): WebClient =
		WebClient
			.builder()
			.baseUrl(webClientProperties.serviceUri.toString())
			.clientConnector(
				ReactorClientHttpConnector(
					HttpClient
						.create()
						.option(
							ChannelOption.CONNECT_TIMEOUT_MILLIS,
							webClientProperties.connectTimeout.toMillisPart()
						)
				)
			)
			.build()

	@Bean
	fun catalogServiceAdapter(
		@Qualifier("catalogServiceWebClient") webClient: WebClient,
	) = CatalogServiceAdapter(
		webClient,
		webClientProperties,
	)
}
