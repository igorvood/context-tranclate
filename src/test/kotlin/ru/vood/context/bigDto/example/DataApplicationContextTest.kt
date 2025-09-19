package ru.vood.context.bigDto.example

import arrow.core.serialization.ArrowModule
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.vood.context.bigDto.example.enrich.enrichContext
import java.time.LocalTime

class DataApplicationContextTest {

    val dataApplicationContext = DataApplicationContext(
        traceId = "traceId",
        activityId = "activityId"
    )

    val json = Json {
        prettyPrint = true
        serializersModule = ArrowModule
    }

    @Test
    fun getTraceId() {

        println(json.encodeToString(DataApplicationContext.serializer(), dataApplicationContext))
        val enriched = dataApplicationContext
            .enrich(enrichContext<DealInfo>(), this::getTraceId)
            .enrich(enrichContext<ParticipantInfo>(), this::getTraceId)
            .enrich(enrichContext<RiskInfo>(), this::getTraceId)
            .enrich(enrichContext<Set<ProductInfo>>(), this::getTraceId)

        println(enriched)


        val assertThrows = Assertions.assertThrows(
            IllegalArgumentException::class.java,
            { enriched.enrich(enrichContext<DealInfo>(), this::testMethod) }
        )

        println(assertThrows.message)

        val assertThrows1 = Assertions.assertThrows(
            IllegalArgumentException::class.java,
            { enriched.enrich(enrichContext<ParticipantInfo>(), this::testMethod) }
        )

        println(assertThrows1.message)

        val enrich = enriched.enrich(enrichContext<RiskInfo>(), this::testMethod)

        println(enrich.mutationInfo)

        val enrich1 = enrich.enrich(enrichContext<Set<ProductInfo>>(), this::testMethod)

        println(enrich1.mutationInfo)

        val encodeToString = json.encodeToString(DataApplicationContext.serializer(), enrich1)
        println(encodeToString)

        val decodeFromString = json.decodeFromString<DataApplicationContext>(encodeToString)

        Assertions.assertEquals(enrich1, decodeFromString)

        val message = enrich1.dealInfo.param()
        val message1 = enrich1.participantInfo.param()
        val message2 = enrich1.productInfo.param()
        val message3 = enrich1.riskInfo.param()
        println("dealInfo = " + message)
        println("participantInfo = " + message1)
        println("productInfo = " + message2)
        println("riskInfo = " + message3)
    }

    @Test
    @Disabled
    fun `тест на пустые значения`() {
        val message = dataApplicationContext.dealInfo.param()
        val message1 = dataApplicationContext.participantInfo.param()
        val message3 = dataApplicationContext.riskInfo.param()
        val message2 = dataApplicationContext.productInfo.param()
        println(message)
        println(message1)
        println(message2)
        println(message3)

    }


    @Test
    fun `нарушен порядок запуска`() {
        val assertThrows = Assertions.assertThrows(
            IllegalArgumentException::class.java,
            { dataApplicationContext.enrich(enrichContext<Set<ProductInfo>>(), this::getTraceId) })

        Assertions.assertEquals(
            "не могу принять ProductInfo, он должен быть принят после dealInfo",
            assertThrows.message
        )

    }


    fun testMethod(s: String, i: Int): LocalTime {
        TODO()
    }

}