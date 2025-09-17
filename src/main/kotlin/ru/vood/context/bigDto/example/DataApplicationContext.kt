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

data class DataApplicationContext(
    val traceId: String,
    val activityId: String,
    val dealInfo: ImmutableNotNullContextParam<DealInfo, SomeError> = pendingImmutableNotNull(),
    val productInfo: MutableNotNullContextParam<Set<ProductInfo>, SomeError> = pendingMutableNotNull(),
    val participantInfo: ImmutableNullableContextParam<ParticipantInfo, SomeError> = pendingImmutableNullable(),
    val riskInfo: MutableNullableContextParam<RiskInfo, SomeError> = pendingMutableNullable(),
) {

    fun enrich(dealInfo: DealInfo): DataApplicationContext {
        val success = this.dealInfo.success(dealInfo)
        return this.copy(dealInfo = success)
    }

    fun enrich(productInfo: Set<ProductInfo>): DataApplicationContext {
        val success = this.productInfo.success(productInfo)
        return this.copy(productInfo = success)
    }

    fun enrich(participantInfo: ParticipantInfo): DataApplicationContext {
        val success = this.participantInfo.success(participantInfo)
        return this.copy(participantInfo = success)
    }

    fun enrich(riskInfo: RiskInfo): DataApplicationContext {
        val success = this.riskInfo.success(riskInfo)
        return this.copy(riskInfo = success)
    }


}