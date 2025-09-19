package ru.vood.context.bigDto

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.reflect.KFunction

/**
 * Реализация [AbstractContextParam] для неизменяемых нулевых параметров.
 * Параметр может быть null даже при успешном выполнении.
 *
 * @param T тип хранимого параметра (может быть nullable)
 * @property param значение параметра, может быть null даже при успешном выполнении
 * @property receivedError сообщение об ошибке, если в процессе получения параметра произошла ошибка
 * @property allreadyReceived флаг указывающий, что данные были полностью получены
 */
@Serializable
data class ImmutableNullableContextParam<T : IContextParam, E : IEnrichError>(
    @Contextual
    override val result: Either<E, T?>? = null,
    override val mutableMethods: List<MutableMethod> = listOf()
) : AbstractContextParam<T, E>() {

    override val mutableParam: Boolean
        get() = false

    override fun param(): T? {
        val either = result ?: error("Parameter not yet available")
        return either.fold(
            { error("Parameter not available due to error: $it") }, { it }
        )
    }

    fun success(
        value: T?,
        method: KFunction<*>
    ): ImmutableNullableContextParam<T, E> {
        require(!this.allreadyReceived()) {
            val last = this.mutableMethods.last()
            "param is immutable, it all ready received in method ${last.methodName} at ${last.time}"
        }

        return this.copy(
            result = value.right(),
            mutableMethods = this.mutableMethods.plus(MutableMethod(method))
        )
    }

    override fun error(
        error: E,
        method: KFunction<*>
    ): ImmutableNullableContextParam<T, E> {
        require(!this.allreadyReceived()) {
            val last = this.mutableMethods.last()
            "param is immutable, it all ready received in method ${last.methodName} at ${last.time}"
        }
        return this.copy(
            result = error.left(),
            mutableMethods = this.mutableMethods.plus(MutableMethod(method))
        )
    }

    /**
     * Создает успешный результат с null значением.
     */
    fun successNull(method: KFunction<*>): ImmutableNullableContextParam<T, E> {
        require(!this.allreadyReceived()) {
            val last = this.mutableMethods.last()
            "param is immutable, it all ready received in method ${last.methodName} at ${last.time}"
        }
        return this.copy(
            result = null.right(),
            mutableMethods = this.mutableMethods.plus(MutableMethod(method))
        )
    }

    companion object {
        /**
         * Создает ожидающий результат (данные еще не получены).
         */
        fun <T : IContextParam, E : IEnrichError> pendingImmutableNullable(): ImmutableNullableContextParam<T, E> {
            return ImmutableNullableContextParam()
        }
    }

}