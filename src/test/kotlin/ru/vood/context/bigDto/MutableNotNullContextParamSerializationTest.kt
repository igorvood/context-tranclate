package ru.vood.context.bigDto

import arrow.core.left
import arrow.core.right
import com.ocadotechnology.gembus.test.Arranger
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import ru.vood.context.bigDto.example.dto.DealInfo
import ru.vood.context.bigDto.example.dto.SomeError

// Дополнительные тесты для проверки сериализации (если нужно)
class MutableNotNullContextParamSerializationTest : FunSpec({
    val dataRight = MutableNotNullContextParam<DealInfo, SomeError>(
        Arranger.some(DealInfo::class.java).right()
    )

    val dataLeft = MutableNotNullContextParam<DealInfo, SomeError>(
        Arranger.some(SomeError::class.java).left()
    )


    val serializer = MutableNotNullContextParam.serializer<DealInfo, SomeError>(
        DealInfo.serializer(),
        SomeError.serializer()
    )
    test("should be serializable right") {
        val encodeToString = json.encodeToString(
            serializer, dataRight
        )
        json.decodeFromString(serializer, encodeToString) shouldBe dataRight
    }

    test("should be serializable left") {
        val encodeToString = json.encodeToString(
            serializer, dataLeft
        )
        json.decodeFromString(serializer, encodeToString) shouldBe dataLeft
    }
})