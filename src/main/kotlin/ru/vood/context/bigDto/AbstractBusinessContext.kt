package ru.vood.context.bigDto

import kotlin.reflect.KFunction
import kotlin.reflect.KProperty1

/**
 * Мета-информация о свойстве контекста, которая определяет:
 * - Свойство параметра контекста в бизнес-контексте
 * - Функцию для обновления свойства (copy-функция)
 * - Зависимости между свойствами (порядок обогащения)
 *
 * @param BC тип бизнес-контекста
 * @param T тип параметра контекста
 * @param E тип ошибки обогащения
 * @param CP тип параметра контекста (AbstractContextParam)
 *
 * @property prop свойство бизнес-контекста, содержащее параметр контекста
 * @property copyFun функция для создания обновленной копии бизнес-контекста с новым значением параметра
 * @property mustEnrichedAfter множество имен свойств, которые должны быть обогащены ДО текущего свойства
 *
 * Пример использования:
 * ```
 * val userMeta = BusinessContext::userParam withCopy { ctx, param -> ctx.copy(userParam = param) } enrichedAfter listOf(BusinessContext::authParam)
 * ```
 */
data class CTXMeta<BC : AbstractBusinessContext<BC>, T : IContextParam?, E : IEnrichError, CP : AbstractContextParam<T, E>>(
    val prop: KProperty1<BC, CP>,
    val copyFun: (BC, CP) -> (BC),
    val mustEnrichedAfter: Set<String> = setOf()
)

/**
 * Инфиксная функция для создания CTXMeta с указанием функции копирования.
 * Связывает свойство бизнес-контекста с функцией для его обновления.
 *
 * @param fc функция копирования, которая принимает текущий контекст и новое значение параметра,
 *           и возвращает обновленную копию контекста
 * @return CTXMeta с установленным свойством и функцией копирования
 *
 * Пример:
 * ```
 * BusinessContext::userParam withCopy { ctx, newParam -> ctx.copy(userParam = newParam) }
 * ```
 */
infix fun <BC : AbstractBusinessContext<BC>, T : IContextParam?, E : IEnrichError, CP : AbstractContextParam<T, E>> KProperty1<BC, CP>.withCopy(
    fc: (BC, CP) -> (BC)
): CTXMeta<BC, T, E, CP> {
    return CTXMeta(this, fc)
}

/**
 * Инфиксная функция для добавления зависимости обогащения.
 * Указывает, что текущее свойство должно быть обогащено ПОСЛЕ указанного свойства.
 *
 * @param otherProp свойство, которое должно быть обогащено перед текущим
 * @return новая CTXMeta с добавленной зависимостью
 * @throws IllegalArgumentException если пытаются добавить зависимость от самого себя
 *
 * Пример:
 * ```
 * userMeta enrichedAfter BusinessContext::authParam
 * ```
 */
infix fun <BC : AbstractBusinessContext<BC>, T : IContextParam?, E : IEnrichError, CP : AbstractContextParam<T, E>> CTXMeta<BC, T, E, CP>.enrichedAfter(
    otherProp: KProperty1<BC, *>
): CTXMeta<BC, T, E, CP> {
    // Защита от циклических зависимостей - свойство не может зависеть от самого себя
    require(this.prop.name != otherProp.name) { "property '${this.prop.name}' cannot be enriched after it self" }
    return this.copy(mustEnrichedAfter = this.mustEnrichedAfter.plus(otherProp.name))
}

/**
 * Инфиксная функция для добавления нескольких зависимостей обогащения.
 * Указывает, что текущее свойство должно быть обогащено ПОСЛЕ всех указанных свойств.
 *
 * @param otherProps список свойств, которые должны быть обогащены перед текущим
 * @return новая CTXMeta с добавленными зависимостями
 *
 * Пример:
 * ```
 * userMeta enrichedAfter listOf(BusinessContext::authParam, BusinessContext::configParam)
 * ```
 */
infix fun <BC : AbstractBusinessContext<BC>, T : IContextParam?, E : IEnrichError, CP : AbstractContextParam<T, E>> CTXMeta<BC, T, E, CP>.enrichedAfter(
    otherProps: List<KProperty1<BC, *>>
): CTXMeta<BC, T, E, CP> = otherProps
    .fold(this) { acc, otherProp ->
        // Последовательно добавляем каждую зависимость
        acc enrichedAfter otherProp
    }

/**
 * Абстрактный базовый класс для бизнес-контекстов.
 * Предоставляет механизм для управления параметрами контекста с поддержкой:
 * - Обогащения параметров (enrichment)
 * - Валидации зависимостей между параметрами
 * - Трассировки изменений (mutation tracking)
 * - Функционального подхода к обновлению состояния
 *
 * @param BC тип конкретного бизнес-контекста (self-type)
 *
 * Основные концепции:
 * 1. Immutability - все изменения создают новые экземпляры
 * 2. Dependency Management - контроль порядка обогащения параметров
 * 3. Functional Updates - использование функций копирования для обновлений
 * 4. Reflection-based Metadata - использование рефлексии для типобезопасности
 */
abstract class AbstractBusinessContext<BC : AbstractBusinessContext<BC>> {

    /**
     * Список мета-информации о всех параметрах контекста.
     * Должен быть переопределен в наследниках для регистрации параметров.
     *
     * Пример реализации:
     * ```
     * override val propsCTXMeta = listOf(
     *     ::userParam withCopy { ctx, param -> ctx.copy(userParam = param) },
     *     ::authParam withCopy { ctx, param -> ctx.copy(authParam = param) } enrichedAfter ::userParam
     * )
     * ```
     */
    abstract val propsCTXMeta: List<CTXMeta<BC, *, *, *>>

    /**
     * Map для быстрого доступа к мета-информации по имени свойства.
     * Ключ - имя свойства, значение - соответствующая CTXMeta
     */
    val propsCTXMetaMap: Map<CTXPropertyName, CTXMeta<BC, *, *, *>> by lazy { propsCTXMeta.associateBy { CTXPropertyName(it.prop.name) } }

    /**
     * Обогащает параметр контекста ошибкой.
     * Создает новый экземпляр бизнес-контекста с обновленным параметром, содержащим ошибку.
     *
     * @param prop свойство, которое нужно обогатить ошибкой
     * @param error ошибка, которую нужно установить
     * @param method метод, в котором произошла ошибка (для трассировки)
     * @return новый экземпляр бизнес-контекста с обновленным параметром
     * @throws IllegalArgumentException если свойство не найдено в мета-информации
     *
     * Пример:
     * ```
     * val updatedContext = context.enrichError(BusinessContext::userParam, UserNotFoundError(), ::fetchUserMethod)
     * ```
     */
    fun <T : IContextParam, E : IEnrichError> enrichError(
        prop: KProperty1<BC, AbstractContextParam<T, E>>,
        error: E,
        method: KFunction<*>
    ): BC {
        // Поиск мета-информации по имени свойства
        val meta: CTXMeta<BC, T, E, AbstractContextParam<T, E>> = (propsCTXMetaMap[CTXPropertyName(prop.name)]
            ?: error("this error un imposible")).let { it as CTXMeta<BC, T, E, AbstractContextParam<T, E>> }

        // Обогащение параметра ошибкой
        val enrichedParam: AbstractContextParam<T, E> = meta.prop.invoke(this as BC).enrichError(error, method)

        // Создание нового экземпляра контекста с обновленным параметром
        return meta.copyFun(this as BC, enrichedParam)
    }

    /**
     * Обогащает параметр контекста успешным значением.
     * Перед обогащением проверяет, что все зависимости удовлетворены.
     *
     * @param prop свойство, которое нужно обогатить
     * @param data данные для установки
     * @param method метод, в котором происходит обогащение (для трассировки)
     * @return новый экземпляр бизнес-контекста с обновленным параметром
     * @throws IllegalArgumentException если:
     *   - свойство не найдено
     *   - не все зависимости удовлетворены
     *
     * Пример:
     * ```
     * val updatedContext = context.enrichOk(BusinessContext::userParam, userData, ::fetchUserMethod)
     * ```
     */
    fun <T : IContextParam?, E : IEnrichError> enrichOk(
        prop: KProperty1<BC, AbstractContextParam<T, E>>,
        data: T,
        method: KFunction<*>
    ): BC {
        // Поиск мета-информации
        val meta: CTXMeta<BC, T, E, AbstractContextParam<T, E>> = (propsCTXMetaMap[CTXPropertyName(prop.name)]
            ?: error("this error un imposible")).let { it as CTXMeta<BC, T, E, AbstractContextParam<T, E>> }

        // Проверка зависимостей - все указанные свойства должны быть уже обогащены
        val notRecived = notRecived(meta)
        require(notRecived.isEmpty()) { "$notRecived must be recived before '${prop.name}'" }

        // Обогащение параметра успешным значением
        val enrichedParam: AbstractContextParam<T, E> = meta.prop.invoke(this as BC).enrichOk(data, method)

        // Создание нового экземпляра контекста
        return meta.copyFun(this as BC, enrichedParam)
    }

    /**
     * Проверяет, какие из зависимых свойств еще не были обогащены.
     *
     * @param meta мета-информация свойства для проверки
     * @return список имен свойств, которые должны быть обогащены, но еще не были
     *
     * Примечание: используется для валидации порядка обогащения параметров
     */
    private fun <T : IContextParam?, E : IEnrichError> notRecived(meta: CTXMeta<BC, T, E, AbstractContextParam<T, E>>): List<String> {
        val filter = meta.mustEnrichedAfter
            .filter { nameAttribute ->
                // Проверяем, было ли свойство уже обогащено (isReceived() == true)
                !(propsCTXMetaMap[CTXPropertyName(nameAttribute)]?.prop?.invoke(this as BC)?.isReceived() ?: false)
            }

        return filter
    }

    /**
     * Собирает информацию о всех мутациях (изменениях) параметров контекста.
     * Возвращает список пар (имя_свойства, информация_о_методе) отсортированный по времени.
     *
     * @return список изменений в хронологическом порядке
     *
     * Использование:
     * - Отладка порядка выполнения
     * - Аудит изменений контекста
     * - Воспроизведение сценариев
     */
    fun mutableMethods(): List<Pair<String, MutableMethod>> {
        return propsCTXMeta.map { it.prop }
            .flatMap { prop ->
                // Для каждого свойства собираем все методы, которые его изменяли
                prop.invoke(this as BC).mutableMethods.map { v -> prop.name to v }
            }
            .sortedBy { it.second.time } // Сортировка по времени изменения
    }

    /**
     * Лениво вычисляемая информация о мутациях.
     * Кэширует результат mutableMethods() для повторного использования.
     */
    val mutationInfo by lazy { mutableMethods() }
}