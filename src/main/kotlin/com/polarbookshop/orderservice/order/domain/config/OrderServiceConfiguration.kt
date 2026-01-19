package com.polarbookshop.orderservice.order.domain.config

import com.polarbookshop.orderservice.catalog.CatalogServiceApi
import com.polarbookshop.orderservice.order.domain.OrderCrudApi
import com.polarbookshop.orderservice.order.domain.OrderService
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class OrderServiceConfiguration {

	@Bean
	fun orderService(
		orderCrudApi: OrderCrudApi,
		catalogServiceApi: CatalogServiceApi,
		streamBridge: StreamBridge,
	) = OrderService(
		orderCrudApi,
		catalogServiceApi,
		streamBridge,
	)
}
