package ru.vood.context.bigDto

import arrow.core.Either
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
sealed class AbstractContextParam<out T : IContextParam, E : IEnrichError>() {

    /**
     * Список методов, изменившие состояние параметра контекста.
     */
    abstract val mutableMethods: List<MutableMethod>

    /**
     * Результат обработки параметра, представленный в виде функционального типа [Either].
     * - [Either.Left] содержит ошибку типа [E] если обработка завершилась неудачно
     * - [Either.Right] содержит параметр типа [T] если обработка прошла успешно
     * Может быть null если параметр еще не получен/вычислен.
     */
    abstract val result: Either<E, T?>?

    /**
     * Указывает, является ли параметр изменяемым.
     * Если true - параметр может быть изменен после создания, иначе параметр immutable.
     */
    abstract val mutableParam: Boolean

    /**
     * Проверяет, был ли параметр уже обработан и результат получен.
     *
     * @return true если результат обработки уже доступен(это может быть как наличие значения так и ошибка его получения/вычисления), false в противном случае
     */
    final fun allreadyReceived(): Boolean = result != null

    /**
     * Возвращает значение параметра если обработка прошла успешно.
     * @throws IllegalStateException если параметр null или есть ошибка
     * @return параметр типа [T] если он успешно обработан, null в противном случае
     */
    abstract fun param(): T?

    /**
     * Создает новый экземпляр параметра контекста с указанной ошибкой.
     * Используется для обработки ошибок при обогащении контекста.
     *
     * @param error ошибка типа [E], которая произошла при обработке
     * @param method функция, в которой произошла ошибка
     * @return новый экземпляр [AbstractContextParam] с установленной ошибкой
     */
    abstract fun error(
        error: E,
        method: KFunction<*>
    ): AbstractContextParam<T, E>

}

