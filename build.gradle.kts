plugins {
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.spring") version "2.1.21"
    kotlin("plugin.serialization") version "2.1.21"
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.devtools.ksp") version "2.1.21-2.0.2"

//    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
}

group = "ru.vood.context.receivers.example"
version = "0.0.1-SNAPSHOT"
description = "context-tranclate"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

val arrowVersion = "2.1.2"
val kotestVersion = "5.8.0"
dependencies {
    implementation(platform ("io.arrow-kt:arrow-stack:$arrowVersion"))
    api("io.arrow-kt:arrow-core")
    api("io.arrow-kt:arrow-core-serialization")
    api("io.arrow-kt:arrow-optics")
    ksp("io.arrow-kt:arrow-optics-ksp-plugin:$arrowVersion")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.ocadotechnology.gembus:test-arranger:1.6.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.8.1!!")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1!!")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1!!")



    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
