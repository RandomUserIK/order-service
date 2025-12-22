package com.polarbookshop.orderservice.order.web.config

import com.polarbookshop.orderservice.order.domain.OrderService
import com.polarbookshop.orderservice.order.web.OrderHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration(proxyBeanMethods = false)
class OrderWebConfiguration {

	@Bean
	fun orderRoutes(orderHandler: OrderHandler) =
		router {
			"/orders".nest {
				GET("", orderHandler::getAllOrders)
			}
		}

	@Bean
	fun orderHandler(
		orderService: OrderService,
	) = OrderHandler(
		orderService,
	)
}