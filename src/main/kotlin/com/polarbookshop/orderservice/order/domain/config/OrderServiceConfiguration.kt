package com.polarbookshop.orderservice.order.domain.config

import com.polarbookshop.orderservice.order.domain.OrderCrudApi
import com.polarbookshop.orderservice.order.domain.OrderService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class OrderServiceConfiguration {

	@Bean
	fun orderService(
		orderCrudApi: OrderCrudApi,
	) = OrderService(
		orderCrudApi,
	)
}
