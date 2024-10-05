# Kbar

## Notice

⚠️ Still actively developing, not ready for use.

## Description

Kbar is A cross-platform **launch bar** written in Kotlin Multiplatform for Desktop, 
for those who want to fully customize logic and user interface.

It provides these features:

- Quick to type a few letters to search. (Configurable in `searchConfig.json`)
- Quick to register global hotkeys. (Configurable in `hotKeyConfig.json`)
- Quick to fetch information from APIs. (Configurable in `apiConfig.json`)

In the following versions, we will provide: 

- Quick to find files and directories. (Still finding out a cross-platform solution)
- Quick to extend your own functions with Kotlin DSL.
- Quick to customize your own theme.

The project is built with powerful linguistic capabilities of Kotlin and its community environment:

- **Compose Desktop**. Build native desktop applications with Jetpack Compose.
- **Ktor**. A powerful and easy-to-use HTTP client for Kotlin.
- **Kotlin DSL**. Write Kotlin code to build your opinionated application.
- **Exposed**. A smart SQL client for Kotlin.

## Distribution

The whole application relies on JVM and has no native-level dependencies. 
Therefore, we can simply package it as a single JAR file and run it as a normal Java application(Just like Minecraft!), 
which means you can run it on any platform without cumbersome distribution(Exe, Msi, Dmg, AppImage, ...).

Currently, a cross-platform build script is being developed, which will automatically build and package the application for all platforms.