plugins {
    kotlin("jvm") version "2.2.20"
    application
    kotlin("plugin.serialization") version "2.2.20"
}

application {
    mainClass.set("org.example.MainKt")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktorVersion = "2.3.7"

dependencies {
    // Ktor server
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")

    // Kotlinx HTML DSL
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}