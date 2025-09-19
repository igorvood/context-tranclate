package ru.vood.context.bigDto.example

import arrow.optics.optics
import kotlinx.serialization.Serializable

@Serializable
@optics
data class DealInfo(
    val id: String
){
    companion object
}
