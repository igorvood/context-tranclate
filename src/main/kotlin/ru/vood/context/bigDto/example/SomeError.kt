package ru.vood.context.bigDto.example

import kotlinx.serialization.Serializable
import ru.vood.context.bigDto.IEnrichError

@Serializable
data class SomeError(val errText: String) : IEnrichError
