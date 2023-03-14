val aapLibVersion = "3.6.30"
val ktorVersion = "2.2.4"

plugins {
    id("io.ktor.plugin")
}

application {
    mainClass.set("app.AppKt")
}

dependencies {
    implementation(project(":domene"))
    implementation(project(":dto-kafka"))

    implementation("com.github.navikt.aap-vedtak:kafka-dto:1.1.1")
    implementation("com.github.navikt.aap-libs:ktor-utils:$aapLibVersion")
    implementation("com.github.navikt.aap-libs:kafka-2:$aapLibVersion")

    implementation("org.apache.kafka:kafka-clients:3.3.1")

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")

    implementation("io.ktor:ktor-server-metrics-micrometer:$ktorVersion")
    implementation("io.micrometer:micrometer-registry-prometheus:1.10.5")

    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("net.logstash.logback:logstash-logback-encoder:7.2")

    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:2.14.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")

    testImplementation(kotlin("test"))
    testImplementation("com.github.navikt.aap-libs:kafka-test-2:$aapLibVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
}
