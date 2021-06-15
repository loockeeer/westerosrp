import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.tools.ant.filters.FixCrLfFilter
import org.apache.tools.ant.filters.ReplaceTokens
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.github.gmazzo.buildconfig") version "3.0.0"
}

group = "fr.westerosrp"
version = "1.0.0"

repositories {
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    maven(url = "https://oss.sonatype.org/content/repositories/central")
}

val minecraft_version: String by project

dependencies {
    // PaperMC Dependency
    compileOnly("org.spigotmc", "spigot-api", "$minecraft_version-R0.1-SNAPSHOT")
}

buildConfig {
    className("BuildConfig")
    packageName("$group.$name")
    val commit = getGitHash()
    val branch = getGitBranch()
    buildConfigField("String", "GIT_COMMIT", "\"$commit\"")
    buildConfigField("String", "GIT_BRANCH", "\"$branch\"")
}

fun getGitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine( "git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString("UTF-8").trim()
}

fun getGitBranch(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine( "git", "rev-parse", "--abbrev-ref", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString("UTF-8").trim()
}

tasks {
    processResources {
        filter(FixCrLfFilter::class)
        filter(ReplaceTokens::class, "tokens" to mapOf("version" to project.version))
        filteringCharset = "UTF-8"
    }
    jar {
        enabled = false
    }
    build {
        dependsOn(shadowJar)
    }
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "16"
        }
    }
}