package ru.vood.context.dtoComposition.context

import java.math.BigDecimal

// DTO для результата
data class CartSummary(
    val clientName: String,
    val totalItems: Int,
    val totalAmount: BigDecimal
)