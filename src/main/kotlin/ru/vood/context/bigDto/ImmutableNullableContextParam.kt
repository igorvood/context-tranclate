package ru.vood.context.bigDto

/**
 * Реализация [AbstractContextParam] для неизменяемых нулевых параметров.
 * Параметр может быть null даже при успешном выполнении.
 *
 * @param T тип хранимого параметра (может быть nullable)
 * @property param значение параметра, может быть null даже при успешном выполнении
 * @property receivedError сообщение об ошибке, если в процессе получения параметра произошла ошибка
 * @property allReadyReceived флаг указывающий, что данные были полностью получены
 */
data class ImmutableNullableContextParam<T, E: IEnrichError>(
    override val param: T? = null,
    override val receivedError: E? = null,
    override val allReadyReceived: Boolean = false,
    override val mutableMethods: List<MutableMethod> = listOf()
) : AbstractContextParam<T, E>() {

    init {
        validateConsistency()
    }

    private fun validateConsistency() {
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
        get() = false

    override fun param(): T? = param
}