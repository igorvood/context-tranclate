package ru.vood.context.bigDto

/**
 * Реализация [AbstractContextParam] для изменяемых ненулевых параметров.
 * Гарантирует что параметр не может быть null когда нет ошибки.
 *
 * @param T тип хранимого параметра (ненулевой)
 * @property param значение параметра, не может быть null при отсутствии ошибки
 * @property receivedError сообщение об ошибке, если в процессе получения параметра произошла ошибка
 * @property allReadyReceived флаг указывающий, что данные были полностью получены
 */
data class MutableNotNullContextParam<T : Any, E: IEnrichError>(
    override val param: T? = null,
    override val receivedError: E? = null,
    override val allReadyReceived: Boolean = param != null || receivedError != null
) : AbstractContextParam<T, E>() {

    init {
        validateConsistency()
    }

    private fun validateConsistency() {
        // Правило 1: Не может быть одновременно и данных и ошибки
        require(!(param != null && receivedError != null)) {
            "Inconsistent state: cannot have both data (param) and error"
        }

        // Правило 2: Если нет ошибки, параметр не может быть null
        require(!(receivedError == null && param == null && allReadyReceived)) {
            "Inconsistent state: parameter cannot be null when there is no error and operation is completed"
        }

        // Правило 3: Если есть ошибка, параметр должен быть null
        require(!(receivedError != null && param != null)) {
            "Inconsistent state: parameter must be null when there is an error"
        }

        // Правило 4: allReadyReceived должен быть согласован с состоянием
        require(allReadyReceived == (param != null || receivedError != null)) {
            "Inconsistent state: allReadyReceived must reflect actual completion state"
        }

        // Правило 5: Если данные получены (allReadyReceived = true), должен быть либо параметр, либо ошибка
        require(!(allReadyReceived && param == null && receivedError == null)) {
            "Inconsistent state: cannot be marked as ready without data or error"
        }
    }

    override val mutableParam: Boolean
        get() = true

    /**
     * Возвращает параметр или бросает исключение если его нет.
     * @throws IllegalStateException если параметр null или есть ошибка
     */
    override fun param(): T {
        return param ?: throw IllegalStateException(
            receivedError?.let { "Parameter not available due to error: $it" }
                ?: "Parameter not yet available"
        )
    }

    /**
     * Проверяет, содержит ли параметр валидное ненулевое значение.
     */
    fun hasValue(): Boolean {
        return param != null && receivedError == null
    }

}