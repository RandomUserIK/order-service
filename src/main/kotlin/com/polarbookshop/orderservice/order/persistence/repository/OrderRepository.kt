package com.polarbookshop.orderservice.order.persistence.repository

import com.polarbookshop.orderservice.order.persistence.OrderEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface OrderRepository : ReactiveCrudRepository<OrderEntity, Long> {
}
