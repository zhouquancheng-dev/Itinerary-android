plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    kotlin("kapt")
    id("kotlinx-serialization")
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

    viewBinding {
        enable = true
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    api(project(":tuiconversation"))
    api(project(":tuichat"))
    api(project(":tuicontact"))
    api(project(":tuigroup"))
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.lifecycle.process)

    api(libs.kotlinx.coroutines.android)
    api(libs.kotlinx.coroutines.guava)
    api(libs.kotlinx.collections.immutable)
    api(libs.kotlin.reflect)

    api(libs.androidx.metrics)

    // hilt不能引入模块使用，在需要使用hilt的模块单独引入依赖
    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.android.compiler)

    api(libs.androidx.datastore.preferences)

    api(libs.kotlinx.serialization.json)

    api(libs.kotlinx.datetime)

    implementation(libs.permissionx)

    api(libs.utilcodex)
    api(libs.toaster)

    api(libs.alicloud.httpdns)

    // 极光
    api(libs.jiguang.jverification)

    implementation(libs.tencent.bugly)

    // Tencent IM
    api(libs.tencent.imsdk.plus)

    implementation(libs.mmkv)

    api(libs.lucksiege.pictureselector)
    api(libs.lucksiege.compress)
    api(libs.lucksiege.ucrop)
    api(libs.lucksiege.camerax)
}