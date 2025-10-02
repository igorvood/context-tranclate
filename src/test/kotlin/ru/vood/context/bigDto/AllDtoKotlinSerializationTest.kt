package ru.vood.context.bigDto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import ru.vood.context.bigDto.example.dto.DataApplicationContext
import ru.vood.kotlin.test.serialisation.AbstractAllDtoSerializationTest
import kotlin.reflect.KClass

@OptIn(InternalSerializationApi::class)
class AllDtoKotlinSerializationTest() : AbstractAllDtoSerializationTest(
    "ru.vood.context",
    Serializable::class,
    { w -> json.encodeToString(w::class.serializer() as KSerializer<Any>, w) },
    { q, w -> json.decodeFromString(w.serializer(), q) }
) {
    override fun getAllSerialisationClasses(): List<KClass<out Any>> {
        return super.getAllSerialisationClasses()
            .filter {
                it.java.canonicalName !in setOf(
                    MutableNotNullContextParam::class,
                    MutableNullableContextParam::class,
                    DataApplicationContext::class,
                    ImmutableNullableContextParam::class,
                    ImmutableNotNullContextParam::class
                ).map { it.java.canonicalName }
            }
    }
}