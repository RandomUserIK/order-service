package com.polarbookshop.orderservice.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache

@EnableWebFluxSecurity
@Configuration(proxyBeanMethods = false)
class SecurityConfiguration {

	@Bean
	fun filterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
		http
			.authorizeExchange {
				it
					.pathMatchers("/actuator/**").permitAll()
					.anyExchange().authenticated()
			}
			.oauth2ResourceServer {
				it.jwt(Customizer.withDefaults())
			}
			.requestCache {
				it.requestCache(NoOpServerRequestCache.getInstance())
			}
			.csrf {
				it.disable()
			}
			.build()
}
