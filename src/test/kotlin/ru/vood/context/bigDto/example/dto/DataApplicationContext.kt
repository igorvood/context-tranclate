package ru.vood.context.bigDto.example.dto

import kotlinx.serialization.Serializable
import ru.vood.context.bigDto.*
import ru.vood.context.bigDto.ImmutableContextParam.Companion.pendingImmutable
import ru.vood.context.bigDto.MutableContextParam.Companion.pendingMutable

@Serializable
data class DataApplicationContext(
    val traceId: String,
    val activityId: String,
    val dealInfo: ImmutableContextParam<DealInfo, SomeError> = pendingImmutable(),
    val productInfo: MutableContextParam<ProductInfos, SomeError> = pendingMutable(),
    val participantInfo: ImmutableContextParam<ParticipantInfo?, SomeError> = pendingImmutable(),
    val riskInfo: MutableContextParam<RiskInfo?, SomeError> = pendingMutable(),
) : AbstractBusinessContext<DataApplicationContext>() {

    override val propsCTXMeta: List<CTXMeta<DataApplicationContext, *, *, *>>
        get() {
            return listOf(
                DataApplicationContext::dealInfo withCopy { q, w -> q.copy(dealInfo = w) },
                DataApplicationContext::productInfo withCopy { q, w -> q.copy(productInfo = w) } enrichedAfter DataApplicationContext::dealInfo,
                DataApplicationContext::participantInfo withCopy { q, w -> q.copy(participantInfo = w) } enrichedAfter DataApplicationContext::productInfo enrichedAfter DataApplicationContext::dealInfo,
                DataApplicationContext::riskInfo withCopy { q, w -> q.copy(riskInfo = w) } enrichedAfter listOf(
                    DataApplicationContext::participantInfo,
                    DataApplicationContext::dealInfo
                )
            )
        }


}