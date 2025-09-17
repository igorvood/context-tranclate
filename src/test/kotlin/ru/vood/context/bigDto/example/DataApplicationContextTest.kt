package ru.vood.context.bigDto.example

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.vood.context.bigDto.example.enrich.enrichContext
import java.time.LocalTime

class DataApplicationContextTest {

    val dataApplicationContext = DataApplicationContext(
        traceId = "traceId",
        activityId = "activityId"
    )

    @Test
    fun getTraceId() {
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

        println(enrich.mutableMethods())

        val enrich1 = enrich.enrich(enrichContext<Set<ProductInfo>>(), this::testMethod)

        println(enrich1.mutableMethods())

    }

    @Test
    fun asdad() {
        val assertThrows = Assertions.assertThrows(
            IllegalArgumentException::class.java,
            { dataApplicationContext.enrich(enrichContext<Set<ProductInfo>>(), this::getTraceId) })

        Assertions.assertEquals("не могу принять ProductInfo, он должен быть принят после dealInfo", assertThrows.message)

    }


    fun testMethod(s: String, i: Int): LocalTime {
        TODO()
    }

}