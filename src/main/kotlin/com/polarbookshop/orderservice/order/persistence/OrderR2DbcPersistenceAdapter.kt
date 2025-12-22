package com.polarbookshop.orderservice.order.persistence

import com.polarbookshop.orderservice.order.domain.Order
import com.polarbookshop.orderservice.order.domain.OrderCrudApi
import com.polarbookshop.orderservice.order.domain.OrderStatus
import com.polarbookshop.orderservice.order.persistence.repository.OrderRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class OrderR2DbcPersistenceAdapter(
	private val orderRepository: OrderRepository,
) : OrderCrudApi {

	override fun findAll(): Flux<Order> =
		orderRepository.findAll().map { it.toDomain() }

	override fun submitOrder(isbn: String, quantity: Int): Mono<Order> =
		Mono.just(buildRejectedOrder(isbn, quantity))
			.flatMap { orderRepository.save(it.toEntity()) }
			.map { it.toDomain() }
}

private fun buildRejectedOrder(bookIsbn: String, quantity: Int) =
	Order(
		bookIsbn = bookIsbn,
		quantity = quantity,
		bookName = null,
		bookPrice = null,
		status = OrderStatus.REJECTED,
	)