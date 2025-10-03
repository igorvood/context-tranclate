package ru.vood.context.bigDto.example

import org.junit.jupiter.api.Test
import ru.vood.context.bigDto.ImmutableContextParam
import ru.vood.context.bigDto.ImmutableContextParam.Companion.pendingImmutable
import ru.vood.context.bigDto.example.dto.DataApplicationContext
import ru.vood.context.bigDto.example.dto.ParticipantInfo
import ru.vood.context.bigDto.example.dto.SomeError
import ru.vood.context.bigDto.example.enrich.enrichContext

class DataApplicationContextErrorTest {

    @Test
    fun enrichNullable() {
        val enrichError = dataApplicationContext
            .enrichError(DataApplicationContext::dealInfo, enrichContext<SomeError>(), this::enrichNullable)
        val participantInfo: ImmutableContextParam<ParticipantInfo?, SomeError> = pendingImmutable()
        val participantInfo1 = participantInfo.enrichOk(null, this::enrichNullable)
        val param = participantInfo1.param()

        println(enrichError)
    }
}