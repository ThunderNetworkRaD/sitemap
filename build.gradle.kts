plugins {
    kotlin("jvm") version "2.0.0"
    id("maven-publish")
}

group = "org.thundernetwork"
version = "1.0.0"

repositories {
    maven("https://repository.thundernetwork.org/repository/maven-public")
}

kotlin {
    jvmToolchain(17)
}

java {
    withSourcesJar()
}

dependencies {
    // testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

//tasks.jar {
//    from(sourceSets.main) // Include all source code in the JAR
//    manifest {
//        attributes(mapOf("Main-Class" to "org.thundernetwork.sitemap"))
//    }
//}

publishing {
    repositories {
        maven {
            url = uri("https://repository.thundernetwork.org/repository/maven-releases")
            credentials {
                username = System.getenv("REPOSITORY_USERNAME")
                password = System.getenv("REPOSITORY_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            pom {
                name = "Sitemap"
                description = "A simply collection of utils for sitemap creation written in Kotlin"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "killerbossoriginal"
                        name = "KillerBossOriginal"
                        email = "killerbossoriginal@thundernetwork.org"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/ThunderNetworkRaD/sitemap.git"
                    developerConnection = "scm:git:ssh://github.com/ThunderNetworkRaD/sitemap.git"
                    url = "https://github.com/ThunderNetworkRaD/sitemap"
                }
                issueManagement {
                    system = "GitHub Issues"
                    url = "https://github.com/ThunderNetworkRaD/sitemap/issues"
                }
                ciManagement {
                    system = "GitHub Actions"
                    url = "https://github.com/ThunderNetworkRaD/sitemap/actions"
                }
                organization {
                    name = "ThunderNetwork"
                    url = "https://thundernetwork.org"
                }
            }
        }
    }
}