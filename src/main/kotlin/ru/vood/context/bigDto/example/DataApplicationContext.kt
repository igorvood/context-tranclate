package ru.vood.context.bigDto.example

import kotlinx.serialization.Serializable
import ru.vood.context.bigDto.*
import ru.vood.context.bigDto.AbstractContextParam.Companion.pendingImmutableNotNull
import ru.vood.context.bigDto.AbstractContextParam.Companion.pendingImmutableNullable
import ru.vood.context.bigDto.AbstractContextParam.Companion.pendingMutableNotNull
import ru.vood.context.bigDto.AbstractContextParam.Companion.pendingMutableNullable
import ru.vood.context.bigDto.AbstractContextParam.Companion.success
import kotlin.reflect.KFunction

@Serializable
data class DataApplicationContext(
    val traceId: String,
    val activityId: String,
    val dealInfo: ImmutableNotNullContextParam<DealInfo, SomeError> = pendingImmutableNotNull(),
    val productInfo: MutableNotNullContextParam<Set<ProductInfo>, SomeError> = pendingMutableNotNull(),
    val participantInfo: ImmutableNullableContextParam<ParticipantInfo, SomeError> = pendingImmutableNullable(),
    val riskInfo: MutableNullableContextParam<RiskInfo, SomeError> = pendingMutableNullable(),
) {

    fun enrich(dealInfo: DealInfo, method: KFunction<*>): DataApplicationContext {
        return this.dealInfo.success(dealInfo, method)
            .let { this.copy(dealInfo = it) }
    }

    fun enrich(productInfo: Set<ProductInfo>, method: KFunction<*>): DataApplicationContext {
        require(dealInfo.allReadyReceived) { "не могу принять ProductInfo, он должен быть принят после dealInfo" }
        return this.productInfo.success(productInfo, method)
            .let { this.copy(productInfo = it) }
    }

    fun enrich(participantInfo: ParticipantInfo, method: KFunction<*>): DataApplicationContext {
        return this.participantInfo.success(participantInfo, method)
            .let { this.copy(participantInfo = it) }
    }

    fun enrich(riskInfo: RiskInfo, method: KFunction<*>): DataApplicationContext {
        return this.riskInfo.success(riskInfo, method)
            .let { this.copy(riskInfo = it) }
    }


    val mutationInfo by lazy { mutableMethods() }

    private fun mutableMethods(): List<Pair<String, MutableMethod>> = dealInfo.mutableMethods.map { "dealInfo" to it }
        .plus(productInfo.mutableMethods.map { "productInfo" to it })
        .plus(participantInfo.mutableMethods.map { "participantInfo" to it })
        .plus(riskInfo.mutableMethods.map { "riskInfo" to it })
        .sortedBy { it.second.time }

}