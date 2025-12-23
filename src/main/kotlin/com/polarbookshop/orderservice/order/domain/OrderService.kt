package com.polarbookshop.orderservice.order.domain

import com.polarbookshop.orderservice.catalog.CatalogServiceApi
import com.polarbookshop.orderservice.catalog.model.Book

class OrderService(
	private val orderCrudApi: OrderCrudApi,
	private val catalogServiceApi: CatalogServiceApi,
) {
	fun getAllOrders() =
		orderCrudApi.findAll()

	fun submitOrder(isbn: String, quantity: Int) =
		catalogServiceApi
			.getBookByIsbn(isbn)
			.map { it.toAcceptedOrder(quantity) }
			.defaultIfEmpty(buildRejectedOrder(isbn, quantity))
			.flatMap(orderCrudApi::submitOrder)

	private fun Book.toAcceptedOrder(quantity: Int) =
		Order(
			bookIsbn = isbn,
			quantity = quantity,
			bookName = "$title - $author",
			bookPrice = price,
			status = OrderStatus.ACCEPTED,
		)

	private fun buildRejectedOrder(isbn: String, quantity: Int) =
		Order(
			bookIsbn = isbn,
			quantity = quantity,
			bookName = null,
			bookPrice = null,
			status = OrderStatus.REJECTED,
		)
}
