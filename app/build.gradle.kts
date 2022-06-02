plugins {
    id("com.github.johnrengelman.shadow")
    application
}

application {
    mainClass.set("no.nav.aap.app.AppKt")
}

dependencies {
    implementation(project(":domene"))

    implementation("com.github.navikt.aap-libs:ktor-utils:0.1.4")
    implementation("com.github.navikt.aap-libs:kafka:0.1.8")

    implementation("io.ktor:ktor-server-core:2.0.1")
    implementation("io.ktor:ktor-server-netty:2.0.1")

    implementation("io.ktor:ktor-server-metrics-micrometer:2.0.1")
    implementation("io.micrometer:micrometer-registry-prometheus:1.9.0")

    implementation("ch.qos.logback:logback-classic:1.2.11")
    runtimeOnly("net.logstash.logback:logstash-logback-encoder:7.2")

    implementation("com.fasterxml.jackson.core:jackson-core:2.13.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.3")

    testImplementation(kotlin("test"))
    testImplementation("com.github.navikt.aap-libs:kafka-test:0.1.4")
    testImplementation("io.ktor:ktor-server-test-host:2.0.1")
    testImplementation("uk.org.webcompere:system-stubs-jupiter:2.0.1")
}

