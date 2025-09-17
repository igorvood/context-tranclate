package ru.vood.context.bigDto.example

import ru.vood.context.bigDto.AbstractContextParam.Companion.pendingImmutableNotNull
import ru.vood.context.bigDto.AbstractContextParam.Companion.pendingImmutableNullable
import ru.vood.context.bigDto.AbstractContextParam.Companion.pendingMutableNotNull
import ru.vood.context.bigDto.AbstractContextParam.Companion.pendingMutableNullable
import ru.vood.context.bigDto.AbstractContextParam.Companion.success
import ru.vood.context.bigDto.ImmutableNotNullContextParam
import ru.vood.context.bigDto.ImmutableNullableContextParam
import ru.vood.context.bigDto.MutableNotNullContextParam
import ru.vood.context.bigDto.MutableNullableContextParam
import kotlin.reflect.KFunction

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


}