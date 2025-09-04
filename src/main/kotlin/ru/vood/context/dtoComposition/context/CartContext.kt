package ru.vood.context.dtoComposition.context

import ru.vood.context.dtoComposition.AbstractBaseContext
import ru.vood.context.dtoComposition.Cart
import ru.vood.context.dtoComposition.Product

// Контекст с корзиной
data class CartContext(
    val clientContext: ClientContext,
    val cart: Cart
) : AbstractBaseContext<CartContext>() {

    val clientId: String get() = clientContext.clientId
    val requestId: String get() = clientContext.requestContext.requestInfo.id

    override fun validate(): Boolean = super.validate() && clientId == cart.clientId && cart.items.isNotEmpty()

    fun withProducts(products: List<Product>): ProductContext {
        validateProductsMatchCart(products)
        return ProductContext(this, products)
    }

    private fun validateProductsMatchCart(products: List<Product>) {
        val cartProductIds = cart.items.map { it.productId }.toSet()
        val providedProductIds = products.map { it.id }.toSet()

        require(cartProductIds == providedProductIds) {
            "Products don't match cart items"
        }
    }

    fun totalItems(): Int = cart.items.sumOf { it.quantity }
    fun isEmpty(): Boolean = cart.items.isEmpty()

    override fun toString(): String = "CartContext(cartId=${cart.id}, items=${totalItems()})"
}
