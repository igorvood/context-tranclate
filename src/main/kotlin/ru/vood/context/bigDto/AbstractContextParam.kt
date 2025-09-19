package ru.vood.context.bigDto

import arrow.core.Either
import kotlin.reflect.KFunction

/**
 * Абстрактный класс представляющий параметр контекста с возможностью хранения данных или ошибки.
 *
 * @param T тип хранимого параметра
 *
 * @property param защищенное свойство для хранения значения параметра. Может быть null если данные отсутствуют или произошла ошибка.
 * @property mutableParam флаг указывающий, является ли параметр изменяемым.
 * @property allReadyReceived флаг указывающий, что данные были полностью получены (успешно или с ошибкой).
 * @property receivedError сообщение об ошибке, если в процессе получения параметра произошла ошибка.
 *
 * @throws IllegalStateException если состояние параметра неконсистентно (одновременно присутствуют и данные и ошибка)
 */
sealed class AbstractContextParam<out T : IContextParam, E : IEnrichError>() {

    abstract val mutableMethods: List<MutableMethod>

    abstract val result: Either<E, T?>?

    /**
     * Флаг указывающий, является ли реализация параметра изменяемой.
     *
     * @return true если параметр можно изменять после создания, false если параметр immutable
     */
    abstract val mutableParam: Boolean

    final fun allReadyReceived(): Boolean = result != null
//            =  param != null || receivedError != null


    /**
     * Проверяет, произошла ли ошибка при получении параметра.
     *
     * @return true если есть сообщение об ошибке, false в противном случае
     *
     * @sample AbstractContextParam.receivedError
     */
    fun hasError() = result?.isLeft()

    /**
     * Проверяет, содержит ли параметр валидное ненулевое значение.
     */
    fun hasValue() = result?.isRight()


    abstract fun param(): T?

//    abstract fun<TT> success(
//        value: TT?,
//        method: KFunction<*>
//    ): AbstractContextParam<T, E>

    abstract fun error(
        error: E,
        method: KFunction<*>
    ): AbstractContextParam<T, E>

}

