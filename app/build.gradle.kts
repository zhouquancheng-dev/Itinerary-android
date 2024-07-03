plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.devtools.ksp)
    id("kotlinx-serialization")
    kotlin("kapt")
}

apply(rootProject.file("buildConfig.gradle.kts"))
val autoConfig: Map<String, Any> by extra

android {
    namespace = "com.zqc.itinerary"
    compileSdk = autoConfig["COMPILE_SDK"] as Int

    defaultConfig {
        applicationId = autoConfig["APPLICATION_ID"].toString()
        minSdk = autoConfig["MIN_SDK"] as Int
        targetSdk = autoConfig["TARGET_SDK"] as Int
        versionCode = autoConfig["VERSION_CODE"] as Int
        versionName = autoConfig["VERSION_NAME"].toString()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        resourceConfigurations.addAll(listOf("en", "zh-rCN"))

        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }

        manifestPlaceholders.putAll(
            mapOf(
                "JPUSH_PKGNAME" to "${autoConfig["APPLICATION_ID"]}",
                "JPUSH_APPKEY" to "${autoConfig["JIGUANG_APPKEY"]}",
                "JPUSH_CHANNEL" to "developer-default"
            )
        )

        buildConfigField("String", "APPLICATION_ID", "\"${autoConfig["APPLICATION_ID"]}\"")
        buildConfigField("String", "PRIVACY_URL", "\"${autoConfig["PRIVACY_URL"]}\"")
        buildConfigField("String", "USER_PROTOCOL_URL", "\"${autoConfig["USER_PROTOCOL_URL"]}\"")
        buildConfigField("String", "FILING_NO", "\"${autoConfig["FILING_NO"]}\"")
        buildConfigField("String", "JIGUANG_APPKEY", "\"${autoConfig["JIGUANG_APPKEY"]}\"")
        buildConfigField("String", "TENCENT_IM_APP_ID", "\"${autoConfig["TENCENT_IM_APP_ID"]}\"")
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField("boolean", "IS_DEBUG", "true")
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField("boolean", "IS_DEBUG", "false")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    kapt {
        correctErrorTypes = true
    }
}

composeCompiler {
    // 启用强跳过模式
    enableStrongSkippingMode = true
    // 启用内在记忆性能优化
    enableIntrinsicRemember = true
    // Compose 编译器将使用该目录转储编译器指标报告
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    // 稳定性配置文件
    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("compose_compiler_config.conf")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:ui"))
    implementation(project(":feature:splash"))
    implementation(project(":feature:login"))
    implementation(project(":feature:im"))
    implementation(project(":feature:mine"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    testImplementation(libs.junit.jupiter.api)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // hilt不能引入模块使用，在需要使用hilt的模块单独引入依赖
    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.android.compiler)
}