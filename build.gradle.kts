plugins {
    kotlin("jvm") version "2.0.0"
}

group = "org.thundernetwork"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://repository.thundernetwork.org/repository/maven-public/")
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}