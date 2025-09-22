package ru.vood.context.bigDto

import kotlin.reflect.KFunction

// Выносим тестовую функцию в область видимости класса
class TestFunctions {
    fun testMethod() = Unit
}

val testFunctions = TestFunctions()
val testMethod: KFunction<*> = testFunctions::testMethod
