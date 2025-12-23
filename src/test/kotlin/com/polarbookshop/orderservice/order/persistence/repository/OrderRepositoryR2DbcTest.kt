package com.polarbookshop.orderservice.order.persistence.repository

import com.polarbookshop.orderservice.order.domain.OrderStatus
import com.polarbookshop.orderservice.order.persistence.OrderEntity
import com.polarbookshop.orderservice.order.persistence.config.OrderPersistenceConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import reactor.test.StepVerifier
import java.time.Instant

@DataR2dbcTest
@Testcontainers
@Import(OrderPersistenceConfiguration::class)
internal class OrderRepositoryR2DbcTest @Autowired constructor(
	private val orderRepository: OrderRepository,
){

	companion object {
		@Container
		@JvmStatic
		val postgresqlContainer = PostgreSQLContainer(
			DockerImageName.parse("postgres:18.1")
		).apply { start() }

		@JvmStatic
		@DynamicPropertySource
		fun postgresqlProperties(registry: DynamicPropertyRegistry) {
			registry.apply {
				add("spring.r2dbc.url") {
					postgresqlContainer.jdbcUrl.replace("jdbc", "r2dbc")
				}
				add("spring.r2dbc.username", postgresqlContainer::getUsername)
				add("spring.r2dbc.password", postgresqlContainer::getPassword)
				add("spring.flyway.url", postgresqlContainer::getJdbcUrl)
			}
		}
	}

	@Test
	fun createRejectedOrder() {
		// GIVEN
		val rejectedOrder = OrderEntity(
			id = null,
			version = 0,
			bookIsbn = "1234567890",
			quantity = 3,
			bookName = null,
			bookPrice = null,
			status = OrderStatus.REJECTED,
			createdDate = Instant.now(),
			lastModifiedDate = Instant.now(),
		)

		// WHEN
		val result = orderRepository.save(rejectedOrder)

		// WHEN
		StepVerifier
			.create(result)
			.expectNextMatches {
				it.id != null &&
				it.version != 0 &&
				it.status == OrderStatus.REJECTED
			}
			.verifyComplete()
	}
}
