package ru.vood.context.bigDto

import arrow.core.Either
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Неизменяемый (immutable) параметр контекста, который гарантированно содержит не-null значение
 * после успешной обработки. Реализует функциональный подход с использованием Either для обработки ошибок.
 *
 * @param T тип параметра контекста, реализующий [IContextParam]
 * @param E тип ошибки, реализующий [IEnrichError]
 * @property result результат обработки параметра в виде [Either], содержащий либо ошибку [E], либо значение параметра [T]
 * @property mutableMethods список методов, которые участвовали в изменении состояния параметра
 *
 * @throws IllegalStateException при попытке получить значение до его установки или при повторной установке значения
 */
@Serializable
data class ImmutableContextParam<T : IContextParam?, E : IEnrichError>(
    @Contextual
    override val result: Either<E, T>? = null,
    override val mutableMethods: List<MutableMethod> = listOf(),
) : AbstractContextParam<T, E>() {

    /**
     * Всегда возвращает false, так как данный параметр является неизменяемым.
     * После установки значения не может быть изменен.
     */
    override val mutableParam: Boolean
        get() = false

    companion object {
        fun <T : IContextParam?, E : IEnrichError> pendingImmutable(): ImmutableContextParam<T, E> {
            return ImmutableContextParam()
        }

    }
}
