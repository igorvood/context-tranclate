package ru.vood.context.bigDto.example

import arrow.optics.optics
import kotlinx.serialization.Serializable
import ru.vood.context.bigDto.IEnrichError

@Serializable
@optics
data class SomeError(val errText: String) : IEnrichError
{
    companion object
}
