package ru.vood.context.Reader_Monad_Kleisli

// Kleisli как обёртка над функциями A -> Reader<R, B>
class Kleisli<R, A, B>(val run: (A) -> Reader<R, B>) {

    fun <C> compose(other: Kleisli<R, C, A>): Kleisli<R, C, B> =
        Kleisli { c -> other.run(c).flatMap { a -> this.run(a) } }

    fun <C> andThen(other: Kleisli<R, B, C>): Kleisli<R, A, C> =
        Kleisli { a -> this.run(a).flatMap { b -> other.run(b) } }

    fun local(f: (R) -> R): Kleisli<R, A, B> =
        Kleisli { a -> this.run(a).local(f) }
}

// Билдер для композиции Kleisli
fun <R, A, B, C> compose(
    f: (A) -> Reader<R, B>,
    g: (B) -> Reader<R, C>
): (A) -> Reader<R, C> = { a -> f(a).flatMap(g) }