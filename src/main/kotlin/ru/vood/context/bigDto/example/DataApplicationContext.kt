package ru.vood.context.bigDto.example

import kotlinx.serialization.Serializable
import ru.vood.context.bigDto.*
import ru.vood.context.bigDto.ImmutableNotNullContextParam.Companion.pendingImmutableNotNull
import ru.vood.context.bigDto.ImmutableNullableContextParam.Companion.pendingImmutableNullable
import ru.vood.context.bigDto.MutableNotNullContextParam.Companion.pendingMutableNotNull
import ru.vood.context.bigDto.MutableNullableContextParam.Companion.pendingMutableNullable
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1

@Serializable
data class DataApplicationContext(
    val traceId: String,
    val activityId: String,
    val dealInfo: ImmutableNotNullContextParam<DealInfo, SomeError> = pendingImmutableNotNull(),
    val productInfo: MutableNotNullContextParam<ProductInfos, SomeError> = pendingMutableNotNull(),
    val participantInfo: ImmutableNullableContextParam<ParticipantInfo, SomeError> = pendingImmutableNullable(),
    val riskInfo: MutableNullableContextParam<RiskInfo, SomeError> = pendingMutableNullable(),
) : IBusinessContext {

    override val includedContextParam: List<AbstractContextParam<*, *>>
        get() = listOf(dealInfo, productInfo, participantInfo, riskInfo)
    override val includedContextProperty: List<KProperty0<AbstractContextParam<*, *>>>
        get() = listOf(
            this::dealInfo,
            this::productInfo,
            this::participantInfo,
            this::riskInfo,
        )


    fun enrich(
        prop: KProperty1<DataApplicationContext, AbstractContextParam<*, *>>,
        someError: SomeError, method: KFunction<*>
    ): DataApplicationContext {
        TODO()
//        return this.dealInfo.success(dealInfo, method)
//            .let { this.copy(dealInfo = it) }
    }


    fun enrich(dealInfo: DealInfo, method: KFunction<*>): DataApplicationContext {
        val property: KProperty0<ImmutableNotNullContextParam<DealInfo, SomeError>> = this::dealInfo
        return this.dealInfo.success(dealInfo, method)
            .let { this.copy(dealInfo = it) }
    }

    fun enrich(productInfo: Set<ProductInfo>, method: KFunction<*>): DataApplicationContext {
        require(dealInfo.allreadyReceived()) { "не могу принять ProductInfo, он должен быть принят после dealInfo" }
        return this.productInfo.success(ProductInfos(productInfo), method)
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

}