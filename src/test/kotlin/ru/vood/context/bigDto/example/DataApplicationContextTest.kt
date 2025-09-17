package ru.vood.context.bigDto.example

import org.junit.jupiter.api.Test
import ru.vood.context.bigDto.example.enrich.enrichContext

class DataApplicationContextTest {

    val dataApplicationContext = DataApplicationContext(
        traceId = "traceId",
        activityId = "activityId"
    )

    @Test
    fun getTraceId() {

        val enriched = dataApplicationContext
            .enrich(enrichContext<DealInfo>())
            .enrich(enrichContext<ParticipantInfo>())
            .enrich(enrichContext<RiskInfo>())
            .enrich(enrichContext<Set<ProductInfo>>())

        println(enriched)


        enriched.enrich(enrichContext<DealInfo>())

    }

}