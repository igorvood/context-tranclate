package ru.vood.context.bigDto

import arrow.core.Either
import arrow.core.right
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.reflect.KFunction
import kotlin.reflect.full.createType

// Тестовые реализации интерфейсов
data class TestParam(val value: String) : IContextParam
data class TestError(val message: String) : IEnrichError

// Выносим тестовую функцию в область видимости класса
class TestFunctions {
    fun testMethod() = Unit
}

class ImmutableNotNullContextParamTest : BehaviorSpec({

    given("ImmutableNotNullContextParam") {

        // Создаем экземпляр класса с функцией
        val testFunctions = TestFunctions()
        val testMethod: KFunction<*> = testFunctions::testMethod

        `when`("создается через pendingImmutableNotNull") {
            then("должен быть в состоянии pending") {
                val param = ImmutableNotNullContextParam.pendingImmutableNotNull<TestParam, TestError>()

                param.result shouldBe null
                param.mutableMethods shouldBe emptyList()
                param.mutableParam shouldBe false
            }
        }

        `when`("вызывается param() на pending параметре") {
            then("должен бросать IllegalStateException") {
                val param = ImmutableNotNullContextParam.pendingImmutableNotNull<TestParam, TestError>()

                val exception = kotlin.runCatching { param.param() }.exceptionOrNull()
                exception.shouldBeInstanceOf<IllegalStateException>()
                exception.message shouldBe "Parameter not yet available"
            }
        }

        `when`("вызывается success с валидным значением") {
            then("должен возвращать новый экземпляр с установленным значением") {
                val initial = ImmutableNotNullContextParam.pendingImmutableNotNull<TestParam, TestError>()
                val testValue = TestParam("test")

                val result = initial.success(testValue, testMethod)

                result.result shouldBe testValue.right()
                result.mutableMethods shouldHaveSize 1
                result.mutableMethods.first().methodName shouldBe "ru.vood.context.bigDto.TestFunctions.testMethod"
            }
        }

        `when`("вызывается success на уже установленном параметре") {
            then("должен бросать IllegalArgumentException") {
                val initial = ImmutableNotNullContextParam.pendingImmutableNotNull<TestParam, TestError>()
                val testValue = TestParam("test")

                val firstSuccess = initial.success(testValue, testMethod)
                val exception = kotlin.runCatching {
                    firstSuccess.success(TestParam("another"), testMethod)
                }.exceptionOrNull()

                exception.shouldBeInstanceOf<IllegalArgumentException>()
                exception.message shouldContain "param is immutable, it all ready received"
            }
        }

//        context("параметризированные тесты для метода param()") {
            withData(
                nameFn = { (value, expected) -> "param() with $value should return $expected" },
                TestParam("hello") to "hello",
                TestParam("world") to "world",
                TestParam("") to "",
                TestParam("123") to "123"
            ) { (input, expected) ->
                val param = ImmutableNotNullContextParam(
                    result = input.right(),
                    mutableMethods = listOf(MutableMethod(testMethod))
                )

                val result = param.param()
                result shouldBe input
                result.value shouldBe expected
            }
//        }

//        context("параметризированные тесты для метода success()") {
            withData(
                nameFn = { (value, methodName) -> "success() with $value should track method $methodName" },
                TestParam("first") to "ru.vood.context.bigDto.TestFunctions.testMethod",
                TestParam("second") to "ru.vood.context.bigDto.TestFunctions.testMethod",
                TestParam("third") to "ru.vood.context.bigDto.TestFunctions.testMethod"
            ) { (value, expectedMethodName) ->
                val initial = ImmutableNotNullContextParam.pendingImmutableNotNull<TestParam, TestError>()

                val result = initial.success(value, testMethod)

                result.result shouldBe value.right()
                result.mutableMethods shouldHaveSize 1
                result.mutableMethods.first().methodName shouldBe expectedMethodName
            }
//        }

        `when`("параметр содержит ошибку") {
            then("param() должен бросать исключение с описанием ошибки") {
                val error = TestError("Something went wrong")
                val param = ImmutableNotNullContextParam(
                    result = Either.Left(error),
                    mutableMethods = listOf(MutableMethod(testMethod))
                )

                val exception = kotlin.runCatching { param.param() }.exceptionOrNull()
                exception.shouldBeInstanceOf<IllegalStateException>()
                exception?.message shouldBe "Parameter not available due to error: $error"
            }
        }

        `when`("проверяется mutableParam") {
            then("всегда должен возвращать false") {
                val pending = ImmutableNotNullContextParam.pendingImmutableNotNull<TestParam, TestError>()
                val withValue = pending.success(TestParam("test"), testMethod)
                val withError = ImmutableNotNullContextParam<TestParam, TestError>(
                    result = Either.Left(TestError("error"))
                )

                pending.mutableParam shouldBe false
                withValue.mutableParam shouldBe false
                withError.mutableParam shouldBe false
            }
        }
    }
})

