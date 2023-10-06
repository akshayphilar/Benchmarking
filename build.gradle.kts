import org.gradle.nativeplatform.platform.internal.Architectures
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    java
    application
    id("me.champeau.jmh").version("0.6.6")

}

group = "com.zyte"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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

sourceSets.main{
    java.srcDirs("src/main/java")
}

val mainClassName = "com.zyte.server.BenchmarkServer"
val mainVerticleName = "com.zyte.server.BenchmarkServer"
val launcherClassName = "io.vertx.core.Launcher"


application {
    mainClass.set(mainClassName)
    applicationDefaultJvmArgs = listOf(// Remote DEBUG and JMX settings
        "--enable-preview",
        "-Dcom.sun.management.jmxremote",
        "-Dcom.sun.management.jmxremote.port=5555",
        "-Dcom.sun.management.jmxremote.rmi.port=37778",
        "-Dcom.sun.management.jmxremote.authenticate=false",
        "-Dcom.sun.management.jmxremote.ssl=false",
        "-Dcom.sun.management.jmxremote.local.only=false",
        "-Djava.rmi.server.hostname=127.0.0.1",
        "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005",
        "-XX:+UseG1GC",
        "-XX:NewRatio=8",
        "-verbose:gc",
        "-XX:ActiveProcessorCount=2",
        "-Xmx4G",
        "-Xms4G"
    )
}


tasks.withType<JavaExec> {
    args = listOf("run", mainVerticleName, "--launcher-class=$launcherClassName")
}