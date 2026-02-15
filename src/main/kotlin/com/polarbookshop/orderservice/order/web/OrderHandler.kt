package com.polarbookshop.orderservice.order.web

import com.polarbookshop.orderservice.order.domain.Order
import com.polarbookshop.orderservice.order.domain.OrderRequest
import com.polarbookshop.orderservice.order.domain.OrderService
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono

class OrderHandler(
	private val orderService: OrderService,
) {

	fun getAllOrders(request: ServerRequest): Mono<ServerResponse> =
		request.principal()
			.cast(Jwt::class.java)
			.map { it.subject }
			.flatMap { subject ->
				orderService.getAllOrders(subject)
					.collectList()
					.flatMap { orders ->
						ok()
							.contentType(MediaType.APPLICATION_JSON)
							.bodyValue(orders)
					}
			}

	fun submitOrder(request: ServerRequest): Mono<ServerResponse> =
		request.bodyToMono<OrderRequest>().flatMap { (bookIsbn, quantity) ->
			ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body<Order>(orderService.submitOrder(bookIsbn, quantity))
		}
}
