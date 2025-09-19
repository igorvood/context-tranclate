package ru.vood.context.bigDto.example

import arrow.optics.optics
import kotlinx.serialization.Serializable
import ru.vood.context.bigDto.IContextParam

@Serializable
@optics
data class ProductInfo(
    val id: String,
) : IContextParam {
    companion object
}
