import java.io.FileInputStream
import java.util.*

plugins {
    `java-library`
//    kotlin("jvm") version "1.9.22"

    `maven-publish`
}

group = "icu.twtool"
version = "1.0-SNAPSHOT"

//repositories {
//    mavenCentral()
//}
dependencies {
    implementation("org.jetbrains:annotations:24.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

publishing {
    publications {
        create<MavenPublication>("mmkv-jvm") {
            from(components["java"])
        }
    }

    val local = Properties().apply {
        load(FileInputStream(File(rootDir, "local.properties")))
    }
    repositories {
        maven {
            val codingArtifactsRepoUrl = "${local["codingArtifactsRepoUrl"]}"
            val codingArtifactsGradleUsername = "${local["codingArtifactsGradleUsername"]}"
            val codingArtifactsGradlePassword = "${local["codingArtifactsGradlePassword"]}"

            url = uri(codingArtifactsRepoUrl)
            credentials {
                username = codingArtifactsGradleUsername
                password = codingArtifactsGradlePassword
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

//kotlin {
//    jvmToolchain(17)
//}