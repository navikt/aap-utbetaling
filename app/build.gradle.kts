import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
    application
}

dependencies {
    implementation(project(":domene"))

    implementation("io.ktor:ktor-server-core:2.0.0")
    implementation("io.ktor:ktor-server-netty:2.0.0")

    implementation("io.ktor:ktor-server-metrics-micrometer:2.0.0")
    implementation("io.micrometer:micrometer-registry-prometheus:1.8.5")

    implementation("no.nav.aap.avro:sokere:3.0.2")
    implementation("no.nav.aap.avro:manuell:0.0.3")
    implementation("no.nav.aap.avro:inntekter:0.0.2")
    implementation("no.nav.aap.avro:medlem:1.1.6")

    implementation("com.sksamuel.hoplite:hoplite-yaml:2.1.2")

    implementation("ch.qos.logback:logback-classic:1.2.11")
    runtimeOnly("net.logstash.logback:logstash-logback-encoder:7.1.1")

    implementation("org.apache.kafka:kafka-clients:3.1.0")
    implementation("org.apache.kafka:kafka-streams:3.1.0")
    constraints {
        implementation("org.rocksdb:rocksdbjni:6.29.4.1") {
            because("Mac M1")
        }
    }
    implementation("io.confluent:kafka-streams-avro-serde:7.1.0") {
        exclude("org.apache.kafka", "kafka-clients")
    }

    implementation("com.fasterxml.jackson.core:jackson-core:2.13.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.2")
    constraints {
        implementation("com.fasterxml.jackson.core:jackson-databind:2.13.2.2") {
            because("2.13.2 vulnerability")
        }
    }

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:2.0.0")
    testImplementation("uk.org.webcompere:system-stubs-jupiter:2.0.1")
    testImplementation("org.apache.kafka:kafka-streams-test-utils:3.1.0")
}

application {
    mainClass.set("no.nav.aap.app.AppKt")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("PASSED", "SKIPPED", "FAILED")
        }
    }
}
