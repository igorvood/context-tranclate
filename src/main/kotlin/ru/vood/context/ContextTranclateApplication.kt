package ru.vood.context

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ContextTranclateApplication

fun main(args: Array<String>) {
    runApplication<ContextTranclateApplication>(*args)
}
