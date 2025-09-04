package ru.vood.context.dtoComposition

interface IContextData<T> {
    fun <R : IContextData<R>> transform(transformer: (T) -> R): R

    fun validate(): Boolean

}