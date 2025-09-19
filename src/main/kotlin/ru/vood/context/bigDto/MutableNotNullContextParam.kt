package ru.vood.context.bigDto

import arrow.core.Either
import arrow.core.left
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
 * @property allreadyReceived флаг указывающий, что данные были полностью получены
 */
@Serializable
data class MutableNotNullContextParam<T : IContextParam, E : IEnrichError>(
    @Contextual
    override val result: Either<E, T>? = null,
    override val mutableMethods: List<MutableMethod> = listOf()
) : AbstractContextParam<T, E>() {

    override val mutableParam: Boolean
        get() = true

    /**
     * Возвращает параметр или бросает исключение если его нет.
     * @throws IllegalStateException если параметр null или есть ошибка
     */
    override fun param(): T {
        return result
            ?.fold(
                { error("Parameter not available due to error: $it") }, {
                    it
                }
            ) ?: error("Parameter not yet available")
    }

    fun success(
        value: T,
        method: KFunction<*>
    ): MutableNotNullContextParam<T, E> {
        return this.copy(
            result = value.right(),
            mutableMethods = this.mutableMethods.plus(MutableMethod(method))
        )
    }

    companion object {
        /**
         * Создает ожидающий результат (данные еще не получены).
         */
        fun <T : IContextParam, E : IEnrichError> pendingMutableNotNull(): MutableNotNullContextParam<T, E> {
            return MutableNotNullContextParam()
        }
    }

}