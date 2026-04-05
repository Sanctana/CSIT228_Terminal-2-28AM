plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "latest.release"
    id("org.beryx.jlink") version "latest.release"
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val junitVersion = "latest.release"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(26)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainClass.set("main.Main")
}

tasks.register<JavaExec>("runApp") {
    group = "application"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("main.Main")
    standardInput = System.`in`
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:latest.release")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
