val ktor_version = "2.3.12"
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.9.10"
    id("io.ktor.plugin") version "2.3.5"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

group = "com.guicarneirodev"
version = "0.0.1"

application {
    mainClass.set("com.guicarneirodev.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveFileName.set("ktor-app-fat.jar")
    mergeServiceFiles()
    manifest {
        attributes(mapOf("Main-Class" to "com.guicarneirodev.ApplicationKt"))
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.sun.mail:javax.mail:1.6.2")
    implementation("org.apache.commons:commons-email:1.5")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.ktor:ktor-server-default-headers:$ktor_version")
    implementation("io.ktor:ktor-server-partial-content:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("com.google.firebase:firebase-admin:9.1.1") {
        exclude(group = "com.google.guava", module = "guava")
    }
    implementation("com.google.guava:guava:32.1.1-jre")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")
    implementation("com.itextpdf:itext7-core:8.0.5")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation ("io.grpc:grpc-netty-shaded:1.51.0")
    implementation ("io.netty:netty-tcnative-boringssl-static:2.0.50.Final")
}
