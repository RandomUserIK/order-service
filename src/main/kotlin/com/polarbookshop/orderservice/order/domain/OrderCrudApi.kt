package com.polarbookshop.orderservice.order.domain

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface OrderCrudApi {
	fun findAll(): Flux<Order>

	fun submitOrder(order: Order): Mono<Order>
}
