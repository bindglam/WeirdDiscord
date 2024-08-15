plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
}

group = "io.github.bindglam"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven(url = "https://repo.codemc.org/repository/maven-public/")
    maven(url = "https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("net.dv8tion:JDA:5.0.2")
    implementation("dev.jorel:commandapi-bukkit-shade:9.5.1")

    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveFileName = "WeirdDiscord.jar"

    dependencies {
        include(dependency("dev.jorel:commandapi-bukkit-shade:9.5.1"))
    }

    relocate("dev.jorel.commandapi", "io.github.bindglam.commandapi")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}