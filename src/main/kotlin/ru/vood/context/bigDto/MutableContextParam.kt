package ru.vood.context.bigDto

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.reflect.KFunction

/**
 * Реализация [AbstractContextParam] для изменяемых ненулевых параметров.
 * Гарантирует что параметр не может быть null когда нет ошибки.
 *
 * @param T тип хранимого параметра (ненулевой)
 * @property param значение параметра, не может быть null при отсутствии ошибки
 * @property receivedError сообщение об ошибке, если в процессе получения параметра произошла ошибка
 * @property isReceived флаг указывающий, что данные были полностью получены
 */
@Serializable
data class MutableContextParam<T : IContextParam?, E : IEnrichError>(
    @Contextual
    override val result: Either<E, T>? = null,
    override val mutableMethods: List<MutableMethod> = listOf()
) : AbstractContextParam<T, E>() {

    override val mutableParam: Boolean
        get() = true

    fun success(
        value: T,
        method: KFunction<*>
    ): MutableContextParam<T, E> {
        return this.copy(
            result = value.right(),
            mutableMethods = this.mutableMethods.plus(MutableMethod(method))
        )
    }

    fun <C> map(f: (right: T) -> C): Either<E, C>? {
        return result?.map { f(it) }
    }

    fun <C> flatMap(f: (right: T) -> Either<E, C>): Either<E, C>? {
        return result?.flatMap { f(it) }
    }

    fun <C> fold(ifLeft: (left: E) -> C, ifRight: (right: T) -> C): C? {
        return result?.fold({ ifLeft(it) }, { ifRight(it) })
    }


    companion object {
        /**
         * Создает ожидающий результат (данные еще не получены).
         */
        fun <T : IContextParam?, E : IEnrichError> pendingMutable(): MutableContextParam<T, E> {
            return MutableContextParam()
        }
    }

}