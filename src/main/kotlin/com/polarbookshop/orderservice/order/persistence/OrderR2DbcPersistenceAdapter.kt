package com.polarbookshop.orderservice.order.persistence

import com.polarbookshop.orderservice.order.domain.Order
import com.polarbookshop.orderservice.order.domain.OrderCrudApi
import com.polarbookshop.orderservice.order.persistence.repository.OrderRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class OrderR2DbcPersistenceAdapter(
	private val orderRepository: OrderRepository,
) : OrderCrudApi {

	override fun findAll(): Flux<Order> =
		orderRepository.findAll().map { it.toDomain() }

	override fun submitOrder(order: Order): Mono<Order> =
		orderRepository
			.save(order.toEntity())
			.map { it.toDomain() }
}
