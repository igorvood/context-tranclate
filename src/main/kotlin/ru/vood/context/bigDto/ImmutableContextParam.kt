package ru.vood.context.bigDto

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.reflect.KFunction

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

    /**
     * Возвращает значение параметра если оно было успешно установлено.
     *
     * @return не-null значение параметра типа [T]
     * @throws IllegalStateException если параметр еще не установлен или содержит ошибку
     */
    override fun param(): T {

        return (result?: error("Parameter not yet available"))
            .fold(
                { error("Parameter not available due to error: $it") }, {
                    it
                }
            )
    }

    /**
     * Устанавливает успешное значение для параметра и возвращает новый экземпляр с обновленным состоянием.
     * Проверяет, что параметр еще не был установлен ранее (так как он immutable).
     *
     * @param value значение параметра для установки
     * @param method функция, в которой устанавливается значение (для трассировки)
     * @return новый экземпляр [ImmutableContextParam] с установленным значением
     * @throws IllegalArgumentException если параметр уже был установлен ранее
     */
    fun enrichOk(
        value: T,
        method: KFunction<*>
    ): ImmutableContextParam<T, E> {
        require(!this.isReceived()) {
            val last = this.mutableMethods.last()
            "param is immutable, it all ready received in method ${last.methodName} at ${last.time}"
        }
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
        fun <T : IContextParam?, E : IEnrichError> pendingImmutable(): ImmutableContextParam<T, E> {
            return ImmutableContextParam()
        }

    }
}
