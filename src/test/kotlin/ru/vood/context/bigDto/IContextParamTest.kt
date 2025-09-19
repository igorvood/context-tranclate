package ru.vood.context.bigDto

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.vood.context.bigDto.MutableNullableContextParam.Companion.pendingMutableNullable

import ru.vood.context.bigDto.example.SomeError

class IContextParamTest {
    @Test
    fun isTNullableTrue() {
        assertTrue(pendingMutableNullable<String?, SomeError>().isTNullable)

    }

    @Test
    @Disabled
    fun isTNullableFalse() {
        assertFalse(pendingMutableNullable<String, SomeError>().isTNullable)

    }

}