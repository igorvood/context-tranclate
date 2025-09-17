package ru.vood.context.bigDto.example

import ru.vood.context.bigDto.IEnrichError

data class SomeError(val errText: String) : IEnrichError
