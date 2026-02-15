package com.polarbookshop.orderservice.order.persistence.repository

import com.polarbookshop.orderservice.order.persistence.OrderEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface OrderRepository : ReactiveCrudRepository<OrderEntity, Long> {

	fun findAllByCreatedBy(userId: String): Flux<OrderEntity>
}
