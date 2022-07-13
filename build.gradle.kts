plugins {
    kotlin("jvm") version "1.7.0"
}

group = "org.hyperskill.ws"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:2.0.3")
    implementation("io.ktor:ktor-server-netty:2.0.3")
    implementation("io.ktor:ktor-server-websockets:2.0.3")

    implementation("com.squareup.okhttp3:okhttp:4.10.0")
}
