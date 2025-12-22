package com.polarbookshop.orderservice.order.domain

class OrderService(
	private val orderCrudApi: OrderCrudApi,
) {
	fun getAllOrders() =
		orderCrudApi.findAll()
}