// build.gradle.kts (Root)
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.6.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
    }
}

dependencyLocking {
    lockAllConfigurations()
}

// Canonical suite entrypoint: `./gradlew test` (used by CI, Makefile, and scanners).
tasks.register("test") {
    group = "verification"
    description = "Runs the Android unit test suite (:app:testDebugUnitTest)"
    dependsOn(":app:testDebugUnitTest")
}

tasks.register("build") {
    group = "build"
    description = "Assembles the debug APK"
    dependsOn(":app:assembleDebug")
}
