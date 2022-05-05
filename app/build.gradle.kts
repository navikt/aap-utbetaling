plugins {
    id("com.github.johnrengelman.shadow")
    application
}

application {
    mainClass.set("no.nav.aap.app.AppKt")
}

dependencies {
    implementation(project(":domene"))

    implementation("com.github.navikt.aap-libs:ktor-utils:0.0.43")
    implementation("com.github.navikt.aap-libs:kafka:0.0.43")

    implementation("io.ktor:ktor-server-core:2.0.1")
    implementation("io.ktor:ktor-server-netty:2.0.1")

    implementation("io.ktor:ktor-server-metrics-micrometer:2.0.1")
    implementation("io.micrometer:micrometer-registry-prometheus:1.8.5")

    implementation("ch.qos.logback:logback-classic:1.2.11")
    runtimeOnly("net.logstash.logback:logstash-logback-encoder:7.1.1")

    implementation("com.fasterxml.jackson.core:jackson-core:2.13.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.2")
    constraints {
        implementation("com.fasterxml.jackson.core:jackson-databind:2.13.2.2") {
            because("2.13.2 vulnerability")
        }
    }

    testImplementation(kotlin("test"))
    testImplementation("com.github.navikt.aap-libs:kafka-test:0.0.43")
    testImplementation("io.ktor:ktor-server-test-host:2.0.0")
    testImplementation("uk.org.webcompere:system-stubs-jupiter:2.0.1")
}

