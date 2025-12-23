package com.polarbookshop.orderservice.order.domain

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class OrderRequest(
	@field:NotBlank(message = "The book ISBN must be defined")
	@field:Pattern(
		regexp = "^([0-9]{10}|[0-9]{13})$",
		message = "The ISBN format must be valid."
	)
	val bookIsbn: String,

	@field:Min(value = 1, message = "You must order at least 1 item")
	@field:Max(value = 5, message = "You cannot order more than 5 items")
	val quantity: Int,
)
