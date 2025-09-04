package ru.vood.context.dtoComposition.context

import ru.vood.context.dtoComposition.AbstractBaseContext
import ru.vood.context.dtoComposition.Cart
import ru.vood.context.dtoComposition.ClientDetails
import ru.vood.context.dtoComposition.PricingResult
import ru.vood.context.dtoComposition.Product
import ru.vood.context.dtoComposition.RequestInfo
import java.math.BigDecimal

data class FullContext(
    val productContext: ProductContext,
    val pricingResult: PricingResult
) : AbstractBaseContext<FullContext>() {

    val clientDetails: ClientDetails get() = productContext.cartContext.clientContext.clientDetails
    val cart: Cart get() = productContext.cartContext.cart
    val products: List<Product> get() = productContext.products
    val requestInfo: RequestInfo get() = productContext.cartContext.clientContext.requestContext.requestInfo

    override fun validate(): Boolean = super.validate() && pricingResult.total >= BigDecimal.ZERO

    fun toSummary(): CartSummary = CartSummary(
        clientName = clientDetails.name,
        totalItems = productContext.cartContext.totalItems(),
        totalAmount = pricingResult.total
    )

    override fun toString(): String = "FullContext(total=${pricingResult.total})"
}
