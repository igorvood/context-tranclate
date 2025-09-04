@file:OptIn(ExperimentalTime::class)

package ru.vood.context.Reader_Monad_Kleisli

import org.springframework.stereotype.Service
import java.math.BigDecimal
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// Сервисы (в реальности это были бы инъекции зависимостей)
interface ClientService {
    fun getClientDetails(clientId: String): ClientDetails
}

interface CartService {
    fun getCart(clientId: String, requestId: String): Cart
}

interface ProductService {
    fun getProducts(productIds: List<String>): List<Product>
}

@Service
// Mock реализации для примера
class MockClientService : ClientService {
    override fun getClientDetails(clientId: String): ClientDetails =
        ClientDetails(clientId, "John Doe", "john@example.com", 2)
}

@Service
class MockCartService : CartService {
    override fun getCart(clientId: String, requestId: String): Cart =
        Cart(clientId, listOf(
            CartItem("prod1", 2),
            CartItem("prod2", 1)
        ), Clock.System.now()
        )
}

@Service
class MockProductService : ProductService {
    override fun getProducts(productIds: List<String>): List<Product> =
        productIds.map { id ->
            Product(id, "Product $id", BigDecimal.valueOf((10 * id.hashCode() % 100).toLong()), "Category")
        }
}