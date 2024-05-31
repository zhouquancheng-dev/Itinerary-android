plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dagger.hilt.android)
    kotlin("kapt")
}

android {
    namespace = "com.example.ui"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

composeCompiler {
    // 启用强跳过模式
    enableStrongSkippingMode = true
    // 启用内在记忆性能优化
    enableIntrinsicRemember = true
    // Compose 编译器将使用该目录转储编译器指标报告，这些指标显示哪些可组合函数是可跳过的、可重新启动的、只读的等等
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    // 稳定性配置文件
    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("compose_compiler_config.conf")
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    api(libs.androidx.constraintlayout.compose)

    api(libs.androidx.activity.compose)
    api(platform(libs.androidx.compose.bom))
    androidTestApi(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.animation)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.material)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material3.window)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.runtime.livedata)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.util)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.ui.graphics.shapes)
    api(libs.androidx.compose.ui.viewbinding)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.material.icons.core)
    api(libs.androidx.compose.material.icons.extended)
    androidTestApi(libs.androidx.compose.ui.test.junit4)
    debugApi(libs.androidx.compose.ui.tooling)
    debugApi(libs.androidx.compose.ui.test.manifest)

    api(libs.airbnb.android.lottie.compose)

    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.android.compiler)
}