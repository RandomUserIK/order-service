package com.polarbookshop.orderservice

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.polarbookshop.orderservice.order.persistence.repository.OrderRepositoryR2DbcTest
import dasniko.testcontainers.keycloak.KeycloakContainer
import org.apache.hc.core5.http.HttpHeaders
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.binder.test.OutputDestination
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Import(
	TestcontainersConfiguration::class,
	TestChannelBinderConfiguration::class,
)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceApplicationTests @Autowired constructor(
	private val objectMapper: ObjectMapper,
	private val outputDestination: OutputDestination,
	private val webTestClient: WebTestClient,
) {

	private lateinit var isabelleToken: KeycloakToken
	private lateinit var bjornToken: KeycloakToken

	@MockitoBean
	private lateinit var catalogServiceWebClient: WebClient

	companion object {
		@Container
		@JvmStatic
		val postgresqlContainer = PostgreSQLContainer(
			DockerImageName.parse("postgres:18.1")
		).apply { start() }

		@Container
		@JvmStatic
		val keycloakContainer = KeycloakContainer(
			"quay.io/keycloak/keycloak:26.4"
		).apply {
			withRealmImportFile("test-realm-config.json")
			start()
		}

		@JvmStatic
		@DynamicPropertySource
		fun dynamicProperties(registry: DynamicPropertyRegistry) {
			registry.apply {
				add("spring.security.oauth2.resourceserver.jwt.issuer-uri") {
					keycloakContainer.authServerUrl + "/realms/PolarBookshop"
				}
				add("spring.r2dbc.url") {
					postgresqlContainer.jdbcUrl.replace("jdbc", "r2dbc")
				}
				add("spring.r2dbc.username", postgresqlContainer::getUsername)
				add("spring.r2dbc.password", postgresqlContainer::getPassword)
				add("spring.flyway.url", postgresqlContainer::getJdbcUrl)
			}
		}

		private fun WebClient.authenticateWith(username: String, password: String) =
			post()
				.body(
					BodyInserters.fromFormData("grant_type", "password")
						.with("client_id", "polar-test")
						.with("username", username)
						.with("password", password)
				)
				.retrieve()
				.bodyToMono<KeycloakToken>()
				.block() ?: error("Could not retrieve keycloak token")
	}

	@BeforeAll
	fun setUp() {
		val webClient = WebClient
			.builder()
			.baseUrl(keycloakContainer.authServerUrl + "/realms/PolarBookshop/protocol/openid-connect/token")
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.build()

		isabelleToken = webClient.authenticateWith("isabelle", "password")
		bjornToken = webClient.authenticateWith("bjorn", "password")
	}

	@Test
	fun contextLoads() {
	}

	private data class KeycloakToken(
		@JsonProperty("access_token")
		val accessToken: String,
	)
}
