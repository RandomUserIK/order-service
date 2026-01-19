package com.polarbookshop.orderservice.order.persistence

import com.polarbookshop.orderservice.order.domain.Order
import com.polarbookshop.orderservice.order.domain.OrderCrudApi
import com.polarbookshop.orderservice.order.persistence.repository.OrderRepository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

open class OrderR2DbcPersistenceAdapter(
	private val orderRepository: OrderRepository,
) : OrderCrudApi {

	override fun findById(id: Long): Mono<Order> =
		orderRepository.findById(id).map { it.toDomain() }

	override fun findAll(): Flux<Order> =
		orderRepository.findAll().map { it.toDomain() }

	@Transactional
	override fun submitOrder(order: Order): Mono<Order> =
		orderRepository
			.save(order.toEntity())
			.map { it.toDomain() }
}
