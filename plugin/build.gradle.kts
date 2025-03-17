plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.jvm)
    id("com.gradle.plugin-publish") version "1.2.1"
}

group = "dk.holonet"
version = "0.0.1"

repositories {
    mavenCentral()
    mavenLocal()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
}

gradlePlugin {
//    website.set("https://github.com/yourusername/holonet-gradle-plugin")
//    vcsUrl.set("https://github.com/yourusername/holonet-gradle-plugin.git")

    plugins {
        create("holonetPlugin") {
            id = "dk.holonet.plugin"
            implementationClass = "dk.holonet.HolonetPlugin"
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}

tasks.withType<PublishToMavenLocal>().configureEach {
    doFirst {
        println("Publishing: ${publication.groupId}:${publication.artifactId}:${publication.version}")
    }
}
