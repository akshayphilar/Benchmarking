import org.gradle.nativeplatform.platform.internal.Architectures
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    id("java")
    id("me.champeau.jmh").version("0.6.6")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


val brotliVersion = "1.12.0"
val operatingSystem: OperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()

dependencies {
    dependencies {
        implementation("com.aayushatharva.brotli4j:brotli4j:$brotliVersion")
        runtimeOnly(
            "com.aayushatharva.brotli4j:native-${
                if (operatingSystem.isWindows) "windows-x86_64"
                else if (operatingSystem.isMacOsX)
                    if (DefaultNativePlatform.getCurrentArchitecture().isArm()) "osx-aarch64"
                    else "osx-x86_64"
                else if (operatingSystem.isLinux)
                    if (Architectures.ARM_V7.isAlias(DefaultNativePlatform.getCurrentArchitecture().name)) "linux-armv7"
                    else if (Architectures.AARCH64.isAlias(DefaultNativePlatform.getCurrentArchitecture().name)) "linux-aarch64"
                    else if (Architectures.X86_64.isAlias(DefaultNativePlatform.getCurrentArchitecture().name)) "linux-x86_64"
                    else
                        throw IllegalStateException("Unsupported architecture: ${DefaultNativePlatform.getCurrentArchitecture().name}")
                else
                    throw IllegalStateException("Unsupported operating system: $operatingSystem")
            }:$brotliVersion"
        )
    }
    implementation("io.vertx:vertx-core:4.4.0")
    implementation("io.vertx:vertx-web:4.4.0")
    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    jmh("org.openjdk.jmh:jmh-core:1.35")
    jmh("org.openjdk.jmh:jmh-generator-annprocess:1.35")
}

tasks.test {
    useJUnitPlatform()
}