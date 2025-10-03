package ru.vood.context.bigDto

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class ImmutableNotNullContextParamPropertyTest : StringSpec({

    "param() should always return the same value that was set by success()" {
        checkAll(Arb.string()) { testString ->
            val initial = ImmutableContextParam.pendingImmutable<TestParam, TestError>()
            val testValue = TestParam(testString)

            val result = initial.enrichOk(testValue, testMethod)
            val retrievedValue = result.param()

            retrievedValue shouldBe testValue
            retrievedValue.value shouldBe testString
        }
    }

    "success() should always add method to mutableMethods" {
        checkAll(Arb.string()) { testString ->
            val initial = ImmutableContextParam.pendingImmutable<TestParam, TestError>()
            val testValue = TestParam(testString)

            val result = initial.enrichOk(testValue, testMethod)

            result.mutableMethods.size shouldBe 1
            result.mutableMethods[0].methodName shouldBe "ru.vood.context.bigDto.TestFunctions.testMethod"
        }
    }
})