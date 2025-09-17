package ru.vood.context.bigDto.example

import org.junit.jupiter.api.Test
import ru.vood.context.bigDto.example.enrich.enrichContext
import java.time.LocalTime
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2
import kotlin.reflect.jvm.javaMethod

class DataApplicationContextTest {

    val dataApplicationContext = DataApplicationContext(
        traceId = "traceId",
        activityId = "activityId"
    )

    @Test
    fun getTraceId() {

        val function: KFunction1<DataApplicationContextTest, Unit> = DataApplicationContextTest::getTraceId
        val function1: KFunction2<String, Int, LocalTime> = this::testMethod
        var javaMethod = DataApplicationContextTest::getTraceId.javaMethod
        javaMethod = this::getTraceId.javaMethod
        val name = function.name
        println(function.javaMethod?.declaringClass?.canonicalName)
        println(function.javaMethod?.name)
        println(name)

        val enriched = dataApplicationContext
            .enrich(enrichContext<DealInfo>())
            .enrich(enrichContext<ParticipantInfo>())
            .enrich(enrichContext<RiskInfo>())
            .enrich(enrichContext<Set<ProductInfo>>())

        println(enriched)


        enriched.enrich(enrichContext<DealInfo>())

    }

    fun testMethod(s: String, i: Int): LocalTime{
        TODO()
    }

}