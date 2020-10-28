plugins {
    id(buildPlugins.androidApplication)
    id(buildPlugins.androidJUnit5)
    kotlin(buildPlugins.android)
    kotlin(buildPlugins.kapt)
    kotlin(buildPlugins.kotlinAndroidExtensions)
}

android {
    compileSdkVersion(androidSdk.compile)
    buildToolsVersion(androidSdk.buildTools)
    defaultConfig {
        applicationId = projectConfig.applicationName
        minSdkVersion(androidSdk.min)
        targetSdkVersion(androidSdk.target)
        versionCode = projectConfig.versionCode
        versionName = projectConfig.versionName
        testInstrumentationRunner = projectConfig.androidJUnitRunner
        testInstrumentationRunnerArgument("runnerBuilder", "de.mannodermaus.junit5.AndroidJUnit5Builder")
    }
    buildTypes {
        projectConfig.buildTypeConfigs.all.forEach { buildTypeConfig ->
            getByName(buildTypeConfig.name) {
                if (buildTypeConfig.minifyEnabled) {
                    isMinifyEnabled = true
                    proguardFiles(getDefaultProguardFile(projectConfig.defaultProguardRulesFileName), projectConfig.proguardRulesFileName)
                }
            }
        }
        val fields = listOf(
            BuildConfigField.StringType(
                name = "GRAVATAR_BASE_URL",
                value = "https://www.gravatar.com/"
            ),
            BuildConfigField.StringType(
                name = "USER_PROFILE_BASE_URL",
                value = "https://private-d71e65-enoccodechallenge.apiary-mock.com/"
            )
        )
        projectConfig.buildTypeConfigs.all.forEach { buildTypeConfig ->
            getByName(buildTypeConfig.name) {
                fields.forEach { field ->
                    field.apply { buildConfigField(type, name, formattedValue) }
                }
            }
        }
    }

    flavorDimensions("type")

    productFlavors{
        create("normal"){
        }
        create("stubbed") {
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        useIR = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libraries.androidX.versions.compose
        kotlinCompilerVersion = kotlinSdk.versions.base
    }
    packagingOptions {
        exclude("META-INF/LICENSE*")
        exclude("META-INF/metadata.kotlin_module")
        exclude("META-INF/metadata.jvm.kotlin_module")
    }
}

dependencies {
    implementation(kotlinSdk.stdlib.core)
    implementation(kotlinSdk.extensions.coroutines.core)
    implementation(kotlinSdk.extensions.coroutines.android)
    implementation(libraries.androidX.core)
    implementation(libraries.androidX.activity)
    implementation(libraries.androidX.appCompat)
    implementation(libraries.androidX.annotation)
    implementation(libraries.androidX.securityCrypto)
    implementation(libraries.androidX.compose.runtime)
    implementation(libraries.androidX.compose.livedata)
    implementation(libraries.androidX.compose.ui)
    implementation(libraries.androidX.compose.material)
    implementation(libraries.androidX.compose.materialIcons)
    implementation(libraries.androidX.compose.tooling)
    implementation(libraries.androidX.lifecycle.runtime)
    implementation(libraries.androidX.lifecycle.livedata)
    implementation(libraries.google.material)

    implementation(libraries.square.retrofit2)
    implementation(libraries.square.retrofit2ConverterMoshi)
    implementation(libraries.square.moshi)
    implementation(libraries.square.moshiKotlinCodegen)

    debugImplementation(libraries.square.leakCanary)
    debugImplementation(libraries.square.okhttp3MockWebServer)

    kapt(libraries.square.moshiKotlinCodegen)

    implementation(libraries.slack.eitherNet)

    implementation(libraries.koin.compose)
    implementation(libraries.banes.accompanist.coil)

    androidTestImplementation(libraries.androidX.test.runner)
    androidTestImplementation(libraries.androidX.test.espresso)
    androidTestImplementation(libraries.junit5.api)
    androidTestImplementation(libraries.mannodermaus.androidTestCore)
    androidTestRuntimeOnly(libraries.mannodermaus.runner)
}

androidExtensions {
    isExperimental = true
}