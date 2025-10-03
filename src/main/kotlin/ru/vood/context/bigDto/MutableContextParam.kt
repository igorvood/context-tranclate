package ru.vood.context.bigDto

import arrow.core.Either
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Реализация [AbstractContextParam] для изменяемых ненулевых параметров.
 * Гарантирует что параметр не может быть null когда нет ошибки.
 *
 * @param T тип хранимого параметра (ненулевой)
 * @property paramOrThrow значение параметра, не может быть null при отсутствии ошибки
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

    companion object {
        /**
         * Создает ожидающий результат (данные еще не получены).
         */
        fun <T : IContextParam?, E : IEnrichError> pendingMutable(): MutableContextParam<T, E> {
            return MutableContextParam()
        }
    }

}