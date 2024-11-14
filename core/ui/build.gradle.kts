import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt.android)
    kotlin("kapt")
}

apply(rootProject.file("buildConfig.gradle.kts"))
val autoConfig: Map<String, Any> by extra

android {
    namespace = "com.example.ui"
    compileSdk = autoConfig["COMPILE_SDK"] as Int

    defaultConfig {
        minSdk = autoConfig["MIN_SDK"] as Int

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
    viewBinding {
        enable = true
    }
    buildFeatures {
        compose = true
    }
}

composeCompiler {
    // 包含来源信息，记录可用于工具确定相应可组合函数的源位置的源信息
    includeSourceInformation = true
    featureFlags = setOf(
        // 启用默认情况下禁用的功能标志
        ComposeFeatureFlag.OptimizeNonSkippingGroups
    )
    // Compose 编译器将使用该目录转储编译器指标报告
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    // 在生成的代码中包含成分跟踪标记，Compose编译器可以将额外的跟踪信息注入到字节码中
    includeTraceMarkers = true
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

    api(libs.androidx.databinding.runtime)

    api(libs.androidx.constraintlayout.compose)

    api(libs.androidx.activity)

    api(libs.androidx.activity.compose)
    api(platform(libs.androidx.compose.bom))
    androidTestApi(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.animation)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.runtime.livedata)
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.ui.util)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.ui.graphics.shapes)
    api(libs.androidx.compose.ui.viewbinding)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.compose.material)
    api(libs.androidx.compose.material.icons.core)
    api(libs.androidx.compose.material.icons.extended)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material3.window)
    api(libs.androidx.compose.material3.android)
    androidTestApi(libs.androidx.compose.ui.test.junit4)
    debugApi(libs.androidx.compose.ui.tooling)
    debugApi(libs.androidx.compose.ui.test.manifest)

    api(libs.androidx.compose.material3.adaptive.navigation.suite)
    api(libs.androidx.compose.material3.adaptive)
    api(libs.androidx.compose.material3.adaptive.layout)
    api(libs.androidx.compose.material3.adaptive.navigation)

    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.androidx.lifecycle.runtime.compose)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.lifecycle.viewmodel.compose)

    // hilt不能引入模块使用，在需要使用hilt的模块单独引入依赖
    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.android.compiler)

    api(libs.androidx.navigation.compose)
    api(libs.androidx.hilt.navigation.compose)
    androidTestApi(libs.androidx.navigation.testing)

    api(libs.lottie.compose)

    api(libs.glide.compose)

    api(libs.compose.placeholder.material3)

    implementation(libs.composetheme)
}