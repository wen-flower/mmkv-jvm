plugins {
    `java-library`
//    kotlin("jvm") version "1.9.22"
}

group = "icu.twtool"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
dependencies {
    implementation("org.jetbrains:annotations:24.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}



//dependencies {
//    testImplementation(kotlin("test"))
//}

tasks.test {
    useJUnitPlatform()
//    jvmArgs("-Djava.library.path=D:\\mmkv-jvm\\cpp\\cmake-build-debug")
}
//kotlin {
//    jvmToolchain(17)
//}