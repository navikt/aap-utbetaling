import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

dependencies {
    api("ch.qos.logback:logback-classic:1.2.11")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "18"
    }

    withType<Test> {
        useJUnitPlatform()
    }
}
