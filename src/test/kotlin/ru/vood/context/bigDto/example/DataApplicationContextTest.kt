package ru.vood.context.bigDto.example

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.vood.context.bigDto.example.dto.*
import ru.vood.context.bigDto.example.enrich.enrichContext
import ru.vood.context.bigDto.json
import java.time.LocalTime

class DataApplicationContextTest {

    @Test
    fun getTraceId() {

        println(json.encodeToString(DataApplicationContext.serializer(), dataApplicationContext))
        val enriched = dataApplicationContext
            .enrichOk(DataApplicationContext::dealInfo, enrichContext<DealInfo>(), this::getTraceId)
            .enrichOk(DataApplicationContext::productInfo,enrichContext<ProductInfos>(), this::getTraceId)
            .enrichOk(DataApplicationContext::participantInfo,enrichContext<ParticipantInfo>(), this::getTraceId)
            .enrichOk(DataApplicationContext::riskInfo,enrichContext<RiskInfo>(), this::getTraceId)


        println(enriched)


        val assertThrows = Assertions.assertThrows(
            IllegalArgumentException::class.java,
            { enriched.enrichOk(DataApplicationContext::dealInfo, enrichContext<DealInfo>(), this::testMethod) }
        )

        println(assertThrows.message)

        val assertThrows1 = Assertions.assertThrows(
            IllegalArgumentException::class.java,
            { enriched.enrichOk(DataApplicationContext::participantInfo,enrichContext<ParticipantInfo>(), this::testMethod) }
        )

        println(assertThrows1.message)

        val enrich = enriched.enrichOk(DataApplicationContext::riskInfo,enrichContext<RiskInfo>(), this::testMethod)

        println(enrich.mutationInfo)

        val enrich1 = enrich.enrichOk(DataApplicationContext::productInfo,enrichContext<ProductInfos>(), this::testMethod)

        println(enrich1.mutationInfo)

        val encodeToString = json.encodeToString(DataApplicationContext.serializer(), enrich1)
        println(encodeToString)

        val decodeFromString = json.decodeFromString<DataApplicationContext>(encodeToString)

        Assertions.assertEquals(enrich1, decodeFromString)

        val message = enrich1.dealInfo.paramOrThrow()
        val message1 = enrich1.participantInfo.paramOrThrow()
        val message2 = enrich1.productInfo.paramOrThrow()
        val message3 = enrich1.riskInfo.paramOrThrow()
        println("dealInfo = " + message)
        println("participantInfo = " + message1)
        println("productInfo = " + message2)
        println("riskInfo = " + message3)
    }

    @Test
    @Disabled
    fun `тест на пустые значения`() {
        val message = dataApplicationContext.dealInfo.paramOrThrow()
        val message1 = dataApplicationContext.participantInfo.paramOrThrow()
        val message3 = dataApplicationContext.riskInfo.paramOrThrow()
        val message2 = dataApplicationContext.productInfo.paramOrThrow()
        println(message)
        println(message1)
        println(message2)
        println(message3)

    }


    @Test
    fun `нарушен порядок запуска`() {
        val assertThrows = Assertions.assertThrows(
            IllegalArgumentException::class.java,
            { dataApplicationContext.enrichOk(DataApplicationContext::productInfo,enrichContext<ProductInfos>(), this::getTraceId) })

        Assertions.assertEquals(
            "[dealInfo] must be recived before 'productInfo'",
            assertThrows.message
        )
    }

    @Test
    fun `нарушен порядок запуска ожидается 2 пополнения`() {
        val assertThrows = Assertions.assertThrows(
            IllegalArgumentException::class.java,
            { dataApplicationContext.enrichOk(DataApplicationContext::riskInfo,enrichContext<RiskInfo>(), this::getTraceId) })

        Assertions.assertEquals(
            "[participantInfo, dealInfo] must be recived before 'riskInfo'",
            assertThrows.message
        )

    }


    fun testMethod(s: String, i: Int): LocalTime {
        TODO()
    }

}