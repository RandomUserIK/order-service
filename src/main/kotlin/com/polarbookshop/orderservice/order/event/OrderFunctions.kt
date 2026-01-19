package com.polarbookshop.orderservice.order.event

import com.polarbookshop.orderservice.order.domain.OrderService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import java.util.function.Consumer

@Configuration(proxyBeanMethods = false)
class OrderFunctions(
	private val orderService: OrderService,
) {
	private val logger = KotlinLogging.logger { }

	@Bean
	fun dispatchOrder(): (Flux<OrderDispatchedMessage>) -> Unit =
		{
			orderService
				.consumeOrderDispatchedEvent(it)
				.doOnNext { logger.info { "The order with id ${it.id} is dispatched" } }
				.subscribe()
		}
}
