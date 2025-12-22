package com.polarbookshop.orderservice.order.domain

import java.time.Instant

data class Order(
	val id: Long? = null,
	val version: Int = 0,
	val bookIsbn: String,
	val bookName: String?,
	val bookPrice: Double?,
	val quantity: Int,
	val status: OrderStatus,
	val createdDate: Instant? = null,
	val lastModifiedDate: Instant? = null,
)
