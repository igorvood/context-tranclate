package ru.vood.context.dtoComposition.context

import ru.vood.context.dtoComposition.AbstractBaseContext
import ru.vood.context.dtoComposition.PricingResult
import ru.vood.context.dtoComposition.Product
import java.math.BigDecimal

data class ProductContext(
    val cartContext: CartContext,
    val products: List<Product>
) : AbstractBaseContext<ProductContext>() {

    private val productMap: Map<String, Product> by lazy { products.associateBy { it.id } }

    override fun validate(): Boolean = super.validate() && products.isNotEmpty()

    fun withPricing(pricing: PricingResult): FullContext {
        require(pricing.total >= BigDecimal.ZERO) { "Invalid pricing result" }
        return FullContext(this, pricing)
    }

    fun getProduct(productId: String): Product =
        productMap[productId] ?: throw IllegalArgumentException("Product not found")

    fun calculateSubtotal(): BigDecimal = cartContext.cart.items.fold(BigDecimal.ZERO) { total, item ->
        total + (getProduct(item.productId).price * BigDecimal(item.quantity))
    }

    override fun toString(): String = "ProductContext(products=${products.size})"
}