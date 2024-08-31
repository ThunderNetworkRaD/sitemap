plugins {
    kotlin("jvm") version "2.0.0"
    id("maven-publish")
}

group = "org.thundernetwork"
version = "1.0.0"

repositories {
    maven("https://repository.thundernetwork.org/repository/maven-central/")
}

kotlin {
    jvmToolchain(17)
}

java {
    withSourcesJar()
}

dependencies {
    implementation(kotlin("stdlib"))
    // testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    from(sourceSets.main.get().allSource)
    manifest {
        attributes(mapOf("Main-Class" to "org.thundernetwork.sitemapKt"))
    }
}

publishing {
    repositories {
        publishing {
            repositories {
                maven {
                    name = "GitHub"
                    url = uri("https://maven.pkg.github.com/ThunderNetworkRaD/sitemap")
                    credentials {
                        username = System.getenv("GITHUB_ACTOR")
                        password = System.getenv("GITHUB_TOKEN")
                    }
                }
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

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