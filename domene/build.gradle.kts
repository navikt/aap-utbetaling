import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

dependencies {
    api("ch.qos.logback:logback-classic:1.2.11")
    implementation("commons-codec:commons-codec:1.15")

    testImplementation(kotlin("test"))
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "18"
    }

    withType<Test> {
        useJUnitPlatform()
    }
}
