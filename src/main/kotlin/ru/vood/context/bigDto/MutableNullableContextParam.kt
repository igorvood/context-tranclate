package ru.vood.context.bigDto

import kotlinx.serialization.Serializable
import kotlin.reflect.KFunction


@Serializable
data class MutableNullableContextParam<T, E: IEnrichError>(
    override val param: T? = null,
    override val receivedError: E? = null,
    override val allReadyReceived: Boolean = false,
    override val mutableMethods: List<MutableMethod> = listOf()
) : AbstractContextParam<T, E>() {

    init {
        // Правило 1: Не может быть одновременно и данных и ошибки
        require(!(param != null && receivedError != null)) {
            "Inconsistent state: cannot have both data (param) and error"
        }

        // Правило 2: Если есть ошибка, allReadyReceived должен быть true
        require(!(receivedError != null && !allReadyReceived)) {
            "Inconsistent state: cannot have error without allReadyReceived = true"
        }

        // Правило 3: Если есть данные, allReadyReceived должен быть true
        require(!(param != null && !allReadyReceived)) {
            "Inconsistent state: cannot have data without allReadyReceived = true"
        }

        // Правило 4: allReadyReceived = true разрешено даже без данных и ошибки (опциональный параметр)
        // Это допустимая ситуация, поэтому не требует проверки
    }

    override val mutableParam: Boolean
        get() = true

    override fun param(): T? = param

    override fun success(
        value: T,
        method: KFunction<*>
    ): MutableNullableContextParam<T, E> {
        return this.copy(
            param = value,
            allReadyReceived = true,
            mutableMethods = this.mutableMethods.plus(MutableMethod(method))
        )
    }

    override fun error(
        error: E,
        method: KFunction<*>
    ): MutableNullableContextParam<T, E> {
        return this.copy(
            receivedError = error,
            allReadyReceived = true,
            mutableMethods = this.mutableMethods.plus(MutableMethod(method))
        )

    }

    companion object{
        /**
         * Создает ожидающий результат (данные еще не получены).
         */
        fun <T, E : IEnrichError> pendingMutableNullable(): MutableNullableContextParam<T, E> {
            return MutableNullableContextParam(allReadyReceived = false)
        }

    }

}
