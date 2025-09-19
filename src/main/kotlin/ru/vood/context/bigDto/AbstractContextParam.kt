package ru.vood.context.bigDto

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
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
abstract sealed class AbstractContextParam<T, E : IEnrichError>() {

    abstract val mutableMethods: List<MutableMethod>

    /**
     * Защищенное свойство для хранения значения параметра.
     *
     * Может быть null в следующих случаях:
     * - данные еще не были получены
     * - в процессе получения данных произошла ошибка
     * - параметр по своей природе может быть null
     */
    protected abstract val param: T?

    /**
     * Флаг указывающий, является ли реализация параметра изменяемой.
     *
     * @return true если параметр можно изменять после создания, false если параметр immutable
     */
    abstract val mutableParam: Boolean

    abstract val allReadyReceived: Boolean
//            =  param != null || receivedError != null

    /**
     * Сообщение об ошибке, возникшей при получении параметра.
     *
     * @return текст ошибки или null если ошибок не было
     */
    abstract val receivedError: E?


    /**
     * Проверяет, произошла ли ошибка при получении параметра.
     *
     * @return true если есть сообщение об ошибке, false в противном случае
     *
     * @sample AbstractContextParam.receivedError
     */
    fun hasError() = receivedError != null

    abstract fun param(): T?

    /**
     * Проверяет, является ли тип T nullable (объявлен с знаком '?').
     * Для определения nullability типа используется рефлексия через класс.
     */
    val isTNullable by lazy { isTypeNullable() }

    /**
     * Вспомогательная функция для определения nullability типа через рефлексию.
     */
    protected fun isTypeNullable(): Boolean {
        return try {
            // Получаем generic superclass
            val superclass = this.javaClass.genericSuperclass
            if (superclass is ParameterizedType) {
                val actualTypeArguments = superclass.actualTypeArguments
                if (actualTypeArguments.isNotEmpty()) {
                    val type = actualTypeArguments[0]
                    // Проверяем, является ли тип nullable
                    isTypeMarkedNullable(type)
                } else {
                    true // Безопасное значение по умолчанию
                }
            } else {
                true // Безопасное значение по умолчанию
            }
        } catch (e: Exception) {
            true // Безопасное значение по умолчанию в случае ошибки
        }
    }

    /**
     * Проверяет, помечен ли тип как nullable.
     */
    private fun isTypeMarkedNullable(type: Type): Boolean {
        return when (type) {
            is Class<*> -> {
                // Для Class - проверяем, не является ли он примитивным типом
                !type.isPrimitive && type != Void.TYPE
            }

            is ParameterizedType -> {
                // Для ParameterizedType проверяем raw type
                val rawType = type.rawType
                if (rawType is Class<*>) {
                    !rawType.isPrimitive && rawType != Void.TYPE
                } else {
                    true
                }
            }

            else -> true // Для WildcardType и других - считаем nullable
        }
    }

    abstract fun success(
    value: T,
    method: KFunction<*>
    ): AbstractContextParam<T, E>

    abstract fun error(
        error: E,
        method: KFunction<*>
    ): AbstractContextParam<T, E>

}

