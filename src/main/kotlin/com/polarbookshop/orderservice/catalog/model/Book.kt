package com.polarbookshop.orderservice.catalog.model

data class Book(
	val isbn: String,
	val title: String,
	val author: String,
	val price: Double,
)
