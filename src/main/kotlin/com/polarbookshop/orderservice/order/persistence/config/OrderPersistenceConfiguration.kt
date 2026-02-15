package com.polarbookshop.orderservice.order.persistence.config

import com.polarbookshop.orderservice.order.persistence.OrderR2DbcPersistenceAdapter
import com.polarbookshop.orderservice.order.persistence.repository.OrderRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext

@Configuration
@EnableR2dbcAuditing
class OrderPersistenceConfiguration {

	@Bean
	fun orderR2DbcPersistenceAdapter(
		orderRepository: OrderRepository,
	) = OrderR2DbcPersistenceAdapter(
		orderRepository,
	)

	@Bean
	fun auditorAware(): ReactiveAuditorAware<String> =
		ReactiveAuditorAware {
			ReactiveSecurityContextHolder
				.getContext()
				.map(SecurityContext::getAuthentication)
				.filter(Authentication::isAuthenticated)
				.map(Authentication::getName)
		}
}
