package ru.vood.context.Reader_Monad_Kleisli

// Базовый Reader Monad
class Reader<R, out A>(val run: (R) -> A) {

    // Functor - преобразование результата
    fun <B> map(f: (A) -> B): Reader<R, B> =
        Reader { r -> f(this.run(r)) }

    // Monad - последовательное выполнение
    fun <B> flatMap(f: (A) -> Reader<R, B>): Reader<R, B> =
        Reader { r -> f(this.run(r)).run(r) }

    // Applicative - применение функции в контексте
    fun <B> ap(ff: Reader<R, (A) -> B>): Reader<R, B> =
        Reader { r -> ff.run(r)(this.run(r)) }

    // Локальное изменение контекста
    fun local(f: (R) -> R): Reader<R, A> =
        Reader { r -> this.run(f(r)) }

    companion object {
        // Получить текущий контекст
        fun <R> ask(): Reader<R, R> = Reader { it }

        // Чтение части контекста
        fun <R, A> asks(f: (R) -> A): Reader<R, A> = Reader(f)

        // Pure - поднять значение в контекст
        fun <R, A> pure(a: A): Reader<R, A> = Reader { _ -> a }
    }
}

// Операторы для удобного использования
infix fun <R, A, B> Reader<R, A>.andThen(next: Reader<R, B>): Reader<R, B> =
    this.flatMap { next }

infix fun <R, A, B> Reader<R, A>.compose(f: (A) -> Reader<R, B>): Reader<R, B> =
    this.flatMap(f)