package com.polarbookshop.orderservice.order.web

import com.polarbookshop.orderservice.order.domain.Order
import com.polarbookshop.orderservice.order.domain.OrderRequest
import com.polarbookshop.orderservice.order.domain.OrderService
import com.polarbookshop.orderservice.order.domain.OrderStatus
import com.polarbookshop.orderservice.order.web.config.OrderWebConfiguration
import com.polarbookshop.orderservice.security.SecurityConfiguration
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import reactor.core.publisher.Mono
import java.time.Instant

@WebFluxTest
@Import(
	OrderWebConfiguration::class,
	SecurityConfiguration::class,
)
internal class OrderHandlerTest @Autowired constructor(
	private val orderHandler: OrderHandler,
	private val webTestClient: WebTestClient,
) {
	@MockitoBean
	private lateinit var orderService: OrderService

	@MockitoBean
	private lateinit var reactiveJwtDecoder: ReactiveJwtDecoder

	@Test
	fun whenBookNotAvailableThenRejectOrder() {
		// GIVEN
		val orderRequest = OrderRequest("1234567890", 1)
		val expectedOrder = Order(
			id = 1,
			version = 1,
			bookIsbn = "1234567890",
			quantity = 3,
			bookName = null,
			bookPrice = null,
			status = OrderStatus.REJECTED,
			createdDate = Instant.now(),
			lastModifiedDate = Instant.now(),
		)

		given(
			orderService.submitOrder(orderRequest.bookIsbn, orderRequest.quantity)
		).willReturn(Mono.just(expectedOrder))

		// WHEN
		webTestClient
			.mutateWith(
				SecurityMockServerConfigurers
					.mockJwt()
					.authorities(SimpleGrantedAuthority("ROLE_customer"))
			)
			.post()
			.uri("/orders")
			.bodyValue(orderRequest)
			.exchange()
			// THEN
			.expectStatus().is2xxSuccessful
			.expectBody<Order>().isEqualTo(expectedOrder)
	}
}
