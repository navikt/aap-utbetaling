plugins {
    `maven-publish`
    `java-library`
}

dependencies {
    implementation("com.github.navikt.aap-libs:kafka-interfaces:3.5.44")
    testImplementation(kotlin("test"))
}

group = "com.github.navikt"

tasks {
    withType<Jar> {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "aap-utbetaling"
            version = project.findProperty("dto-kafka.version").toString()
            from(components["java"])
        }

        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/navikt/aap-utbetaling")
                credentials {
                    username = "x-access-token"
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}
