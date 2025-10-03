package ru.vood.context.bigDto

import arrow.core.Either
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Реализация [AbstractContextParam] для изменяемых ненулевых параметров.
 * Гарантирует что параметр не может быть null когда нет ошибки.
 *
 * Основные характеристики:
 * - Значение может быть изменено многократно в течение жизненного цикла
 * - Поддерживает состояние "ожидания", "успеха" и "ошибки"
 * - Сохраняет историю всех изменений через mutableMethods
 * - Использует Either для функциональной обработки результатов
 *
 * @param T тип хранимого параметра, может быть null
 * @param E тип ошибки, реализующий [IEnrichError]
 * @property result результат обработки параметра в виде [Either], содержащий либо ошибку [E], либо значение параметра [T]
 * @property mutableMethods список методов, которые участвовали в изменении состояния параметра
 *
 * @see AbstractContextParam базовый класс с общей логикой
 * @see ImmutableContextParam неизменяемая версия параметра
 */
@Serializable
data class MutableContextParam<T : IContextParam?, E : IEnrichError>(
    @Contextual
    override val result: Either<E, T>? = null,
    override val mutableMethods: List<MutableMethod> = listOf()
) : AbstractContextParam<T, E>() {

    /**
     * Всегда возвращает true, так как данный параметр является изменяемым.
     * Значение может быть обновлено многократно через методы enrichOk/enrichError.
     *
     * Это ключевое отличие от ImmutableContextParam - позволяет гибко обновлять
     * значение параметра в процессе работы приложения.
     */
    override val mutableParam: Boolean
        get() = true

    companion object {
        /**
         * Создает новый экземпляр MutableContextParam в начальном состоянии "ожидания".
         * Параметр готов к последующим обновлениям значения через enrichOk/enrichError.
         *
         * @return MutableContextParam<T, E> с пустым результатом и пустым списком методов
         *
         * Пример использования:
         * ```
         * val userParam = MutableContextParam.pendingMutable<UserContext, ValidationError>()
         * // Позже можно обновить значение
         * val updatedParam = userParam.enrichOk(userData, ::updateUserMethod)
         * ```
         */
        fun <T : IContextParam?, E : IEnrichError> pendingMutable(): MutableContextParam<T, E> {
            return MutableContextParam()
        }
    }
}