package ru.vood.context.dtoComposition


// Абстрактный базовый класс с общей логикой
abstract class AbstractBaseContext<T : IContextData<T>> : IContextData<T> {
    override fun <R : IContextData<R>> transform(transformer: (T) -> R): R {
        @Suppress("UNCHECKED_CAST")
        return transformer(this as T)
    }

    override fun validate(): Boolean = true

    // Общие утилитные методы
    fun log(message: String) {
        println("[$this] $message")
    }

    fun <R> withValidation(block: (T) -> R): R {
        require(validate()) { "Context validation failed" }
        @Suppress("UNCHECKED_CAST")
        return block(this as T)
    }
}