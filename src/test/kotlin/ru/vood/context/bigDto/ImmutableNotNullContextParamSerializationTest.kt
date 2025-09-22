package ru.vood.context.bigDto

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

// Дополнительные тесты для проверки сериализации (если нужно)
class ImmutableNotNullContextParamSerializationTest : FunSpec({
    test("should be serializable") {
        // Здесь можно добавить тесты на сериализацию, если используется kotlinx.serialization
        // Для примера просто проверяем, что класс помечен аннотацией @Serializable
        ImmutableNotNullContextParam::class.annotations.any {
            it.annotationClass.simpleName == "Serializable"
        } shouldBe true
    }
})