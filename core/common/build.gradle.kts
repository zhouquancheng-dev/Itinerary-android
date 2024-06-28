plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dagger.hilt.android)
    kotlin("kapt")
}

apply(rootProject.file("buildConfig.gradle.kts"))
val autoConfig: Map<String, Any> by extra

android {
    namespace = "com.example.common"
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

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.lifecycle.process)

    api(libs.kotlinx.coroutines.android)

    api(libs.kotlinx.collections.immutable)

    // hilt不能引入模块使用，在需要使用hilt的模块单独引入依赖
    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.android.compiler)

    api(platform(libs.coil.kt.bom))
    api(libs.coil.kt.compose)
    api(libs.coil.kt.gif)
    api(libs.coil.kt.video)
    api(libs.coil.kt.svg)

    api(libs.androidx.datastore.preferences)

    // 极光认证
    api(libs.jiguang.jverification)

    api(libs.bcprov.jdk15on)

    api(libs.utilcodex)
    api(libs.toaster)

    implementation(libs.tencent.bugly)
    api(libs.alicloud.httpdns)
}