@file:OptIn(ExperimentalTime::class)

package ru.vood.context.Reader_Monad_Kleisli

import java.math.BigDecimal
import kotlin.time.ExperimentalTime

class Application {
    private val clientService = MockClientService()
    private val cartService = MockCartService()
    private val productService = MockProductService()
    private val calculator = CartCalculator(clientService, cartService, productService)

    fun processRequest(clientId: String) {
        val requestContext = RequestContext(
            requestId = "req-123",
            authToken = "bearer-token-abc"
        )

        // Использование Reader Monad
        val totalCalculation: Reader<RequestContext, BigDecimal> =
            calculator.calculateTotal(clientId)

        val result = totalCalculation.run(requestContext)
        println("Total amount: $result")

        // Композиция нескольких операций
        val fullPipeline: Reader<RequestContext, EnrichedContext> =
            calculator.enrichContext(clientId)
                .local { it.copy(requestId = "modified-req-id") } // модификация контекста

        val enriched = fullPipeline.run(requestContext)
        println("Enriched context: $enriched")
    }

    // Пример с Kleisli композицией
    fun kleisliExample(clientId: String) {
        val getClient: (String) -> Reader<RequestContext, ClientDetails> = { id ->
            Reader { ctx -> clientService.getClientDetails(id) }
        }

        val getCart: (ClientDetails) -> Reader<RequestContext, Cart> = { client ->
            Reader { ctx -> cartService.getCart(client.id, ctx.requestId) }
        }

        val getProducts: (Cart) -> Reader<RequestContext, List<Product>> = { cart ->
            val productIds = cart.items.map { it.productId }
            Reader { ctx -> productService.getProducts(productIds) }
        }

        val calculate: (List<Product>) -> Reader<RequestContext, BigDecimal> = { products ->
            Reader { _ -> products.sumOf { it.price } } // упрощённый расчёт
        }

        // Композиция Kleisli стрелок
        val pipeline = compose(getClient, compose(getCart, compose(getProducts, calculate)))

        val requestContext = RequestContext("req-456", "token-xyz")
        val result = pipeline(clientId).run(requestContext)
        println("Kleisli result: $result")
    }
}