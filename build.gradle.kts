plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.beryx.jlink") version "2.25.0"
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val junitVersion = "5.12.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.21")
    implementation(project(":common"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
