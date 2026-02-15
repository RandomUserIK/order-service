package com.polarbookshop.orderservice.order.persistence

import com.polarbookshop.orderservice.order.domain.Order
import com.polarbookshop.orderservice.order.domain.OrderStatus
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("orders")
data class OrderEntity(
	@Id
	val id: Long?,

	@Version
	val version: Int,

	val bookIsbn: String,

	val bookName: String?,

	val bookPrice: Double?,

	val quantity: Int,

	val status: OrderStatus,

	@CreatedDate
	val createdDate: Instant,

	@LastModifiedDate
	val lastModifiedDate: Instant,

	@CreatedBy
	val createdBy: String?,

	@LastModifiedBy
	val lastModifiedBy: String?,
)

internal fun OrderEntity.toDomain() =
	Order(
		id = id,
		version = version,
		bookIsbn = bookIsbn,
		bookName = bookName,
		bookPrice = bookPrice,
		quantity = quantity,
		status = status,
		createdDate = createdDate,
		lastModifiedDate = lastModifiedDate,
		createdBy = createdBy,
		lastModifiedBy = lastModifiedBy,
	)

internal fun Order.toEntity() =
	OrderEntity(
		id = id,
		version = version,
		bookIsbn = bookIsbn,
		bookName = bookName,
		bookPrice = bookPrice,
		quantity = quantity,
		status = status,
		createdDate = createdDate ?: Instant.now(),
		lastModifiedDate = lastModifiedDate ?: Instant.now(),
		createdBy = createdBy,
		lastModifiedBy = lastModifiedBy,
	)
