@file:OptIn(ExperimentalTime::class)

package ru.vood.context.Reader_Monad_Kleisli

import java.math.BigDecimal
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

// Модели данных
data class RequestContext(
    val requestId: String,
    val authToken: String,
    val timestamp: Instant = Clock.System.now()
)

data class ClientDetails(
    val id: String,
    val name: String,
    val email: String,
    val loyaltyLevel: Int
)

data class Cart(
    val clientId: String,
    val items: List<CartItem>,
    val createdAt: Instant
)

data class CartItem(
    val productId: String,
    val quantity: Int
)

data class Product(
    val id: String,
    val name: String,
    val price: BigDecimal,
    val category: String
)

data class EnrichedContext(
    val request: RequestContext,
    val client: ClientDetails,
    val cart: Cart,
    val products: List<Product>
)