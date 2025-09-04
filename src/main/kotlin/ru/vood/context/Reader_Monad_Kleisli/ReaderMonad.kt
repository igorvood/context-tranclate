package ru.vood.context.Reader_Monad_Kleisli

import java.math.BigDecimal

class CartCalculator(
    private val clientService: ClientService,
    private val cartService: CartService,
    private val productService: ProductService
) {

    // Получение деталей клиента
    fun getClientDetails(clientId: String): Reader<RequestContext, ClientDetails> =
        Reader.ask<RequestContext>().map { ctx ->
            clientService.getClientDetails(clientId)
        }

    // Получение корзины
    fun getCart(clientId: String): Reader<RequestContext, Cart> =
        Reader.ask<RequestContext>().map { ctx ->
            cartService.getCart(clientId, ctx.requestId)
        }

    // Получение продуктов
    fun getProducts(productIds: List<String>): Reader<RequestContext, List<Product>> =
        Reader.ask<RequestContext>().map { ctx ->
            productService.getProducts(productIds)
        }

    // Обогащение контекста
    fun enrichContext(clientId: String): Reader<RequestContext, EnrichedContext> {
//        val flatMap: Reader<RequestContext, EnrichedContext> = Reader { ctx ->
//            val client = clientService.getClientDetails(clientId)
//            val cart = cartService.getCart(clientId, "ctx.requestId")
//            val productIds = cart.items.map { it.productId }
//            val products = productService.getProducts(productIds)
//
//            EnrichedContext(ctx, client, cart, products)
//        }.flatMap<EnrichedContext> { enriched ->
//            Reader.pure(enriched)
//        }
//        return flatMap
        TODO()
    }

    // Расчет общей стоимости
    fun calculateTotal(clientId: String): Reader<RequestContext, BigDecimal> =
        getClientDetails(clientId).flatMap { client ->
            getCart(clientId).flatMap { cart ->
                val productIds = cart.items.map { it.productId }
                getProducts(productIds).map { products ->
                    calculateTotalAmount(cart, products)
                }
            }
        }

    private fun calculateTotalAmount(cart: Cart, products: List<Product>): BigDecimal {
        val productMap = products.associateBy { it.id }
        return cart.items.fold(BigDecimal.ZERO) { total, item ->
            val product = productMap[item.productId] ?: throw IllegalStateException("Product not found")
            total + (product.price * BigDecimal(item.quantity))
        }
    }

    // Альтернативный стиль с for-comprehension (в Kotlin - sequence)
    fun calculateTotalAlternative(clientId: String): Reader<RequestContext, BigDecimal> =
        Reader { ctx ->
            val client = clientService.getClientDetails(clientId)
            val cart = cartService.getCart(clientId, ctx.requestId)
            val productIds = cart.items.map { it.productId }
            val products = productService.getProducts(productIds)

            calculateTotalAmount(cart, products)
        }
}