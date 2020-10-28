@file:Suppress("ClassName")

object projectConfig {
    const val applicationName = "com.aidanvii.codechallenge"
    const val versionCode = 1
    const val versionName = "1.0"
    const val defaultProguardRulesFileName = "proguard-android-optimize.txt"
    const val proguardRulesFileName = "proguard-rules.pro"
    const val androidJUnitRunner = "androidx.test.runner.AndroidJUnitRunner"

    object buildTypeConfigs {

        private val debug = BuildTypeConfig(
            name = "debug",
            minifyEnabled = false
        )

        private val release = BuildTypeConfig(
            name = "release",
            minifyEnabled = true
        )

        val all = listOf(debug, release)
    }
}

object kotlinSdk {

    object versions {
        const val base = "1.4.10"
        const val coroutines = "1.4.0-M1"
    }

    private const val kotlinGroup = "org.jetbrains.kotlin"

    object stdlib {
        const val core = "$kotlinGroup:kotlin-stdlib:${versions.base}"
    }

    const val junit = "$kotlinGroup:kotlin-test-junit:${versions.base}"

    object extensions {
        private const val kotlinXGroup = "org.jetbrains.kotlinx"

        object coroutines {
            private const val prefix = "$kotlinXGroup:kotlinx-coroutines"
            const val core = "$prefix-core:${versions.coroutines}"
            const val android = "$prefix-android:${versions.coroutines}"
            const val test = "$prefix-test:${versions.coroutines}"
        }
    }
}

object buildPlugins {

    private object versions {
        const val gradle = "4.2.0-alpha13"
        const val junit5 = "1.6.2.0"
    }

    const val androidApplication = "com.android.application"
    const val android = "android"
    const val kotlinAndroidExtensions = "android.extensions"
    const val androidJUnit5 = "de.mannodermaus.android-junit5"
    const val androidGradle = "com.android.tools.build:gradle:${versions.gradle}"
    const val androidJupiter = "de.mannodermaus.gradle.plugins:android-junit5:${versions.junit5}"
    const val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinSdk.versions.base}"
    const val kapt = "kapt"
}

object androidSdk {
    const val min = 26
    const val compile = 30
    const val target = compile
    const val buildTools = "30.0.2"
}

object libraries {
    object androidX {
        object versions {
            const val core = "1.5.0-alpha04"
            const val annotation = "1.2.0-alpha01"
            const val appCompat = "1.3.0-alpha02"
            const val lifecycle = "2.3.0-alpha06"
            const val compose = "1.0.0-alpha04"
            const val activity = "1.2.0-beta01"
            const val securityCrypto = "1.0.0-rc03"
        }

        const val activity = "androidx.activity:activity-ktx:${versions.activity}"
        const val annotation = "androidx.annotation:annotation:${versions.annotation}"
        const val appCompat = "androidx.appcompat:appcompat:${versions.appCompat}"
        const val core = "androidx.core:core-ktx:${versions.core}"
        const val securityCrypto = "androidx.security:security-crypto:${versions.securityCrypto}"

        object compose {
            private const val prefix = "androidx.compose"
            const val ui = "$prefix.ui:ui:${versions.compose}"
            const val runtime = "$prefix.runtime:runtime:${versions.compose}"
            const val livedata = "$prefix.runtime:runtime-livedata:${versions.compose}"
            const val material = "$prefix.material:material:${versions.compose}"
            const val materialIcons = "$prefix.material:material-icons-extended:${versions.compose}"
            const val tooling = "androidx.ui:ui-tooling:${versions.compose}"
        }

        object lifecycle {
            private const val prefix = "androidx.lifecycle"
            const val runtime = "$prefix:lifecycle-runtime-ktx:${versions.lifecycle}"
            const val livedata = "$prefix:lifecycle-livedata-ktx:${versions.lifecycle}"
        }

        object test {
            const val runner = "androidx.test:runner:1.3.0"
            const val espresso = "androidx.test.espresso:espresso-core:3.3.0"
        }
    }

    object google {
        private object versions {
            const val material = "1.2.1"
        }

        const val material = "com.google.android.material:material:${versions.material}"
    }

    object koin {

        private object versions {
            const val koin = "2.2.0-rc-2"
        }

        private const val prefix = "org.koin"
        const val core = "$prefix:koin-core:${versions.koin}"
        const val compose = "$prefix:koin-androidx-compose:${versions.koin}"
        const val test = "$prefix:koin-test:${versions.koin}"
    }

    object square {
        private object versions {
            const val leakCanary = "2.5"
            const val retrofit2 = "2.9.0"
            const val okhttp3MockWebServer = "4.9.0"
            const val moshi = "1.11.0"
        }

        private const val prefix = "com.squareup"
        const val leakCanary = "$prefix.leakcanary:leakcanary-android:${versions.leakCanary}"
        const val retrofit2 = "$prefix.retrofit2:retrofit:${versions.retrofit2}"
        const val okhttp3MockWebServer = "$prefix.okhttp3:mockwebserver:${versions.okhttp3MockWebServer}"
        const val retrofit2ConverterMoshi = "$prefix.retrofit2:converter-moshi:${versions.retrofit2}"
        const val moshi = "$prefix.moshi:moshi:${versions.moshi}"
        const val moshiKotlinCodegen = "$prefix.moshi:moshi-kotlin-codegen:${versions.moshi}"
    }

    object slack {
        private object versions {
            const val eitherNet = "0.2.0"
        }

        private const val prefix = "com.slack"
        const val eitherNet = "$prefix.eithernet:eithernet:${versions.eitherNet}"
    }

    object banes {
        private object versions {
            const val accompanist = "0.3.0"
        }

        object accompanist {
            private const val prefix = "dev.chrisbanes.accompanist:accompanist"
            const val coil = "$prefix-coil:${versions.accompanist}"
        }
    }

    object junit5 {

        private object versions {
            const val junit5 = "5.7.0"
        }

        private const val prefix = "org.junit.jupiter"
        const val api = "$prefix:junit-jupiter-api:${versions.junit5}"
        const val params = "$prefix:junit-jupiter-params:${versions.junit5}"
        const val engine = "$prefix:junit-jupiter-engine:${versions.junit5}"
    }

    object mannodermaus {
        const val version = "1.2.0"
        const val androidTestCore = "de.mannodermaus.junit5:android-test-core:$version"
        const val runner = "de.mannodermaus.junit5:android-test-runner:$version"
    }
}