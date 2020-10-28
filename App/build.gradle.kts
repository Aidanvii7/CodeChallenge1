// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath(buildPlugins.androidGradle)
        classpath(buildPlugins.kotlinGradle)
        classpath(buildPlugins.androidJupiter)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}

subprojects {
    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf(
                    "-Xinline-classes",
                    "-Xopt-in=kotlin.experimental.ExperimentalTypeInference",
                    "-Xopt-in=kotlin.RequiresOptIn"
                )
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}