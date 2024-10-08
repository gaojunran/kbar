import org.gradle.internal.impldep.com.fasterxml.jackson.core.JsonPointer.compile
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val ktor_version: String by project

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    kotlin("plugin.serialization") version "2.0.20"
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            implementation("io.ktor:ktor-client-core:$ktor_version")
            implementation("io.ktor:ktor-client-cio:$ktor_version")
            implementation("io.ktor:ktor-client-encoding:$ktor_version")
            implementation("com.github.tulskiy:jkeymaster:1.3")
            implementation("org.slf4j:slf4j-jdk14:1.7.13")
            implementation("com.jayway.jsonpath:json-path:2.9.0")
            implementation("com.dorkbox:SystemTray:4.4")
            implementation(libs.exposed.core)
            implementation(libs.exposed.jdbc)
            implementation("org.xerial:sqlite-jdbc:3.46.1.3")
        }
    }
}


compose.desktop {

    application {
        mainClass = "gaojunran.kbar.MainKt"

        // Thank you: https://github.com/JetBrains/compose-multiplatform/issues/3818
        buildTypes.release.proguard {
            version.set("7.6.0") // Or 7.5.0 when it's published for compatibility with Kotlin 2.0.0
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.AppImage)
            packageName = "gaojunran.kbar"
            packageVersion = "1.0.0"
        }
    }
}

