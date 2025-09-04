package ru.vood.context.dtoComposition

import java.math.BigDecimal
import java.time.Instant


// Модели данных
data class RequestInfo(
    val id: String,
    val timestamp: Instant = Instant.now(),
    val authToken: String? = null,
    val metadata: Map<String, String> = emptyMap()
) {
    init {
        require(id.isNotBlank()) { "Request ID cannot be blank" }
    }
}

data class ClientDetails(
    val id: String,
    val name: String,
    val email: String,
    val loyaltyLevel: Int = 0
)

data class CreditCardDetails(
    val id: String,
)

data class Cart(
    val id: String,
    val clientId: String,
    val items: List<CartItem> = emptyList()
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

data class PricingResult(
    val subtotal: BigDecimal,
    val discount: BigDecimal,
    val total: BigDecimal
)