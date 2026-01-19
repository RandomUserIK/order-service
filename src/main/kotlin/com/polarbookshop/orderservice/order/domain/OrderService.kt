package com.polarbookshop.orderservice.order.domain

import com.polarbookshop.orderservice.catalog.CatalogServiceApi
import com.polarbookshop.orderservice.catalog.model.Book
import com.polarbookshop.orderservice.order.event.OrderAcceptedMessage
import com.polarbookshop.orderservice.order.event.OrderDispatchedMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux

class OrderService(
	private val orderCrudApi: OrderCrudApi,
	private val catalogServiceApi: CatalogServiceApi,
	private val streamBridge: StreamBridge,
) {
	private val logger = KotlinLogging.logger { }

	fun getAllOrders() =
		orderCrudApi.findAll()

	fun submitOrder(isbn: String, quantity: Int) =
		catalogServiceApi
			.getBookByIsbn(isbn)
			.map { it.toAcceptedOrder(quantity) }
			.defaultIfEmpty(buildRejectedOrder(isbn, quantity))
			.flatMap(orderCrudApi::submitOrder)
			.doOnNext(::publishOrderAcceptedEvent)

	fun consumeOrderDispatchedEvent(orderDispatchedFlux: Flux<OrderDispatchedMessage>): Flux<Order> =
		orderDispatchedFlux
			.flatMap { dispatchedOrder ->
				orderCrudApi
					.findById(dispatchedOrder.id)
					.map { it.toDispatchedOrder() }
					.flatMap(orderCrudApi::submitOrder)
			}

	fun publishOrderAcceptedEvent(order: Order) {
		order.apply {
			if (status != OrderStatus.ACCEPTED) {
				return@apply
			}

			val orderAcceptedMessage = toAcceptedMessage()
			val result = streamBridge.send("acceptOrder-out-0", orderAcceptedMessage)
			logger.info { "Result of sending data for order with id: $id: $result" }
		}
	}

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

	private fun Order.toDispatchedOrder() =
		when (status == OrderStatus.DISPATCHED) {
			true -> this
			else -> copy(status = OrderStatus.DISPATCHED)
		}

	private fun Order.toAcceptedMessage() =
		OrderAcceptedMessage(id ?: error("Order $this does not have an ID. ID is mandatory"))
			.also { logger.info { "Sending order accepted event with id: ${it.id}" } }
}
