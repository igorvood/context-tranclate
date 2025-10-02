package ru.vood.context.bigDto.example

import org.junit.jupiter.api.Test
import ru.vood.context.bigDto.example.dto.DataApplicationContext
import ru.vood.context.bigDto.example.dto.SomeError
import ru.vood.context.bigDto.example.enrich.enrichContext

class DataApplicationContextErrorTest {

    @Test
    fun getTraceId() {
        val enrichError = dataApplicationContext
            .enrichError(DataApplicationContext::dealInfo, enrichContext<SomeError>(), this::getTraceId)


        println(enrichError)
    }
}