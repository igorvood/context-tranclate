package ru.vood.context.bigDto

import arrow.core.Either
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
data class ImmutableNotNullContextParam<T : IContextParam, E : IEnrichError>(
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
        return result
            ?.fold(
                { error("Parameter not available due to error: $it") }, {
                    it
                }
            ) ?: error("Parameter not yet available")
    }

    /**
     * Устанавливает успешное значение для параметра и возвращает новый экземпляр с обновленным состоянием.
     * Проверяет, что параметр еще не был установлен ранее (так как он immutable).
     *
     * @param value значение параметра для установки
     * @param method функция, в которой устанавливается значение (для трассировки)
     * @return новый экземпляр [ImmutableNotNullContextParam] с установленным значением
     * @throws IllegalArgumentException если параметр уже был установлен ранее
     */
    fun success(
        value: T,
        method: KFunction<*>
    ): ImmutableNotNullContextParam<T, E> {
        require(!this.isReceived()) {
            val last = this.mutableMethods.last()
            "param is immutable, it all ready received in method ${last.methodName} at ${last.time}"
        }
        return this.copy(
            result = value.right(),
            mutableMethods = this.mutableMethods.plus(MutableMethod(method))
        )
    }

    companion object {
        fun <T : IContextParam, E : IEnrichError> pendingImmutableNotNull(): ImmutableNotNullContextParam<T, E> {
            return ImmutableNotNullContextParam()
        }

    }
}
