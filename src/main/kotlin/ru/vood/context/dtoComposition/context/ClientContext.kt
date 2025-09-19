package ru.vood.context.dtoComposition.context

import ru.vood.context.dtoComposition.AbstractBaseContext
import ru.vood.context.dtoComposition.Cart
import ru.vood.context.dtoComposition.ClientDetails

data class ClientContext(
    val requestContext: RequestContext,
    val clientId: String,
    val clientDetails: ClientDetails
) : AbstractBaseContext<ClientContext>() {

    override fun validate(): Boolean = super.validate() && clientId == clientDetails.id

    fun withCart(cart: Cart): CartContext {
        require(clientId == cart.clientId) { "Cart does not belong to client" }
        require(cart.items.isNotEmpty()) { "Cart cannot be empty" }
        return CartContext(this, cart)
    }

    fun isPremium(): Boolean = clientDetails.loyaltyLevel >= 3

    override fun toString(): String = "ClientContext(clientId=$clientId, name=${clientDetails.name})"
}

