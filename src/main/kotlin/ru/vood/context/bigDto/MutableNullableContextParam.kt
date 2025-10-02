package ru.vood.context.bigDto

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.reflect.KFunction


@Serializable
data class MutableNullableContextParam<T : IContextParam, E : IEnrichError>(
    @Contextual
    override val result: Either<E, T?>? = null,
    override val mutableMethods: List<MutableMethod> = listOf()
) : AbstractContextParam<T, E>() {

    override val mutableParam: Boolean
        get() = true

    override fun param(): T? {
        val either = result ?: error("Parameter not yet available")
        return either.fold(
            { error("Parameter not available due to error: $it") }, { it }
        )
    }

    fun success(
        value: T?,
        method: KFunction<*>
    ): MutableNullableContextParam<T, E> {
        return this.copy(
            result = value.right(),
            mutableMethods = this.mutableMethods.plus(MutableMethod(method))
        )
    }

    /**
     * Создает успешный результат с null значением.
     */
    fun successNull(method: KFunction<*>): MutableNullableContextParam<T, E> = this.success(null, method)


    fun <C> map(f: (right: T?) -> C): Either<E, C>? {
        return result?.map { f(it) }
    }

    fun <C> flatMap(f: (right: T?) -> Either<E, C>): Either<E, C>? {
        return result?.flatMap { f(it) }
    }
    fun <C> fold(ifLeft: (left: E) -> C, ifRight: (right: T?) -> C): C? {
        return result?.fold({ ifLeft(it) }, { ifRight(it) })
    }


    companion object {
        /**
         * Создает ожидающий результат (данные еще не получены).
         */
        fun <T : IContextParam, E : IEnrichError> pendingMutableNullable(): MutableNullableContextParam<T, E> {
            return MutableNullableContextParam()
        }

    }

}
