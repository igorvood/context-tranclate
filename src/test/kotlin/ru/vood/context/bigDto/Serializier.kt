package ru.vood.context.bigDto

import arrow.core.serialization.ArrowModule
import kotlinx.serialization.json.Json

val json = Json {
    prettyPrint = true
    serializersModule = ArrowModule
}