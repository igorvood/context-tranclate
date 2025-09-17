package ru.vood.context.bigDto

data class MutableNullableContextParam<T, E: IEnrichError>(
    override val param: T? = null,
    override val receivedError: E? = null,
    override val allReadyReceived: Boolean = false
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

}
