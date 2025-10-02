package ru.vood.context.bigDto.example.dto

import arrow.optics.optics
import kotlinx.serialization.Serializable
import ru.vood.context.bigDto.IContextParam

@Serializable
@optics
data class ProductInfos(
    val productInfos: Set<ProductInfo>
) : IContextParam {
    companion object
}
