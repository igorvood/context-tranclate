package ru.vood.context.bigDto.example

import arrow.optics.optics
import kotlinx.serialization.Serializable

@Serializable
@optics
data class ProductInfo(
    val id: String,
)
{
    companion object
}
