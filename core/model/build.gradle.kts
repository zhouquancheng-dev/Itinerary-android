plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.devtools.ksp)
    id("kotlinx-serialization")
    kotlin("kapt")
}

apply(rootProject.file("buildConfig.gradle.kts"))
val autoConfig: Map<String, Any> by extra

android {
    namespace = "com.example.model"
    compileSdk = autoConfig["COMPILE_SDK"] as Int

    defaultConfig {
        minSdk = autoConfig["MIN_SDK"] as Int

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        resourceConfigurations.addAll(listOf("en", "zh-rCN"))
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.squareup.moshi)
    ksp(libs.squareup.moshi.kotlin.codegen)

    implementation(libs.squareup.retrofit2.converter.kotlinx.serialization)
}