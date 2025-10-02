package ru.vood.context.bigDto.example.enrich

import com.ocadotechnology.gembus.test.Arranger

inline fun <reified T> enrichContext() = Arranger.some<T>(T::class.java)