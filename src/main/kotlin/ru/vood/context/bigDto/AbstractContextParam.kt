package ru.vood.context.bigDto

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import kotlin.reflect.KFunction

/**
 * Абстрактный базовый класс для параметров контекста, который предоставляет функциональность
 * для обработки и валидации параметров с поддержкой функционального подхода через Either.
 *
 * @param T тип параметра контекста, реализующий [IContextParam]
 * @param E тип ошибки, реализующий [IEnrichError]
 *
 * @property mutableMethods список методов, изменившие состояние параметра
 * @property result результат обработки параметра в виде [Either], содержащий либо ошибку [E], либо значение параметра [T]
 * @property mutableParam флаг, указывающий является ли параметр изменяемым
 */
sealed class AbstractContextParam<T : IContextParam?, E : IEnrichError>() {

    /**
     * Список методов, изменившие состояние параметра контекста.
     * Каждый элемент содержит информацию о методе, который модифицировал состояние параметра.
     */
    abstract val mutableMethods: List<MutableMethod>

    /**
     * Результат обработки параметра, представленный в виде функционального типа [Either].
     * - [Either.Left] содержит ошибку типа [E] если обработка завершилась неудачно
     * - [Either.Right] содержит параметр типа [T] если обработка прошла успешно
     * Может быть null если параметр еще не получен/вычислен.
     */
    abstract val result: Either<E, T>?

    /**
     * Указывает, является ли параметр изменяемым.
     * Если true - параметр может быть изменен после создания, иначе параметр immutable.
     */
    abstract val mutableParam: Boolean

    /**
     * Проверяет, был ли параметр уже обработан и результат получен.
     *
     * @return true если результат обработки уже доступен (это может быть как наличие значения так и ошибка его получения/вычисления),
     *         false в противном случае
     */
    final fun isReceived(): Boolean = result != null

    /**
     * Возвращает значение параметра если обработка прошла успешно.
     *
     * @throws IllegalStateException если параметр null или есть ошибка
     * @return параметр типа [T] если он успешно обработан
     */
    fun paramOrThrow(): T {
        return (result ?: error("Parameter not yet available"))
            .fold(
                ifLeft = { error("Parameter not available due to error: $it") },
                ifRight = { it }
            )
    }

    /**
     * Возвращает результат обработки параметра.
     *
     * @throws IllegalStateException если результат еще не доступен
     * @return результат типа [Either] содержащий либо ошибку, либо значение параметра
     */
    fun resultOrThrow(): Either<E, T> = result ?: error("Result not yet available")

    /**
     * Создает новый экземпляр параметра контекста с указанной ошибкой.
     * Используется для обработки ошибок при обогащении контекста.
     *
     * @param error ошибка типа [E], которая произошла при обработке
     * @param method функция, в которой произошла ошибка
     * @return новый экземпляр [AbstractContextParam] с установленной ошибкой
     */
    fun enrichError(
        error: E,
        method: KFunction<*>
    ): AbstractContextParam<T, E> = enrich({ error.left() }, method)

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
    ): AbstractContextParam<T, E> = enrich({ value.right() }, method)

    /**
     * Основной метод для обогащения параметра контекста.
     * Принимает функцию, которая возвращает результат обработки в виде Either,
     * и добавляет информацию о вызванном методе в историю изменений.
     *
     * @param f функция, возвращающая результат обработки параметра
     * @param method функция, которая выполняет обогащение (для трассировки)
     * @return новый экземпляр [AbstractContextParam] с обновленным состоянием
     */
    fun enrich(f: () -> Either<E, T>, method: KFunction<*>): AbstractContextParam<T, E> = when (this) {
        // Обработка для неизменяемого параметра
        is ImmutableContextParam<T, E> -> {
            // Проверяем, что параметр еще не был установлен
            assertImmutable()
            // Создаем копию с новым результатом и обновленной историей методов
            this.copy(
                result = f(),
                mutableMethods = this.mutableMethods.plus(MutableMethod(method))
            )
        }
        // Обработка для изменяемого параметра
        is MutableContextParam<T, E> -> this.copy(
            result = f(),
            mutableMethods = this.mutableMethods.plus(MutableMethod(method))
        )
    }

    /**
     * Вспомогательный метод для проверки, что неизменяемый параметр еще не был установлен.
     *
     * @throws IllegalArgumentException если параметр уже был получен/установлен
     */
    private fun AbstractContextParam<T, E>.assertImmutable() {
        require(!this.isReceived()) {
            val last = this.mutableMethods.last()
            "param is immutable, it all ready received in method ${last.methodName} at ${last.time}"
        }
    }
}
