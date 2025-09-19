package ru.vood.context.bigDto

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.reflect.KFunction

@Serializable
data class ImmutableNotNullContextParam<T : IContextParam, E : IEnrichError>(
    @Contextual
    override val result: Either<E, T>? = null,
    override val mutableMethods: List<MutableMethod> = listOf(),
) : AbstractContextParam<T, E>() {

    override val mutableParam: Boolean
        get() = false

    override fun param(): T {
        return result
            ?.fold(
                { error("Parameter not available due to error: $it") }, {
                    it
                }
            ) ?: error("Parameter not yet available")
    }

    fun success(
        value: T,
        method: KFunction<*>
    ): ImmutableNotNullContextParam<T, E> {
        require(!this.allreadyReceived()) {
            val last = this.mutableMethods.last()
            "param is immutable, it all ready received in method ${last.methodName} at ${last.time}"
        }
        return this.copy(
            result = value.right(),
            mutableMethods = this.mutableMethods.plus(MutableMethod(method))
        )
    }

    companion object {
        /**
         * Создает ожидающий результат (данные еще не получены).
         */
        fun <T : IContextParam, E : IEnrichError> pendingImmutableNotNull(): ImmutableNotNullContextParam<T, E> {
            return ImmutableNotNullContextParam()
        }

    }
}
