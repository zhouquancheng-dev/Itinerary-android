import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    kotlin("kapt")
    id("kotlinx-serialization")
    id("aleyn-router")
}

apply(rootProject.file("buildConfig.gradle.kts"))
val autoConfig: Map<String, Any> by extra

val keyStoreFile = rootProject.file("keystore.properties")
val keyStoreProperties = Properties()
keyStoreProperties.load(FileInputStream(keyStoreFile))

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
                "JPUSH_CHANNEL" to "developer-default",
                "APP_NAME" to "${autoConfig["APP_NAME"]}"
            )
        )
        buildConfigField("String", "APP_NAME", "\"${autoConfig["APP_NAME"]}\"")
        buildConfigField("String", "APPLICATION_ID", "\"${autoConfig["APPLICATION_ID"]}\"")
        buildConfigField("String", "PRIVACY_URL", "\"${autoConfig["PRIVACY_URL"]}\"")
        buildConfigField("String", "USER_PROTOCOL_URL", "\"${autoConfig["USER_PROTOCOL_URL"]}\"")
        buildConfigField("String", "FILING_NO", "\"${autoConfig["FILING_NO"]}\"")
        buildConfigField("String", "JIGUANG_APPKEY", "\"${autoConfig["JIGUANG_APPKEY"]}\"")
        buildConfigField("String", "TENCENT_IM_APP_ID", "\"${autoConfig["TENCENT_IM_APP_ID"]}\"")
    }

    signingConfigs {
        create("releaseConfig") {
            storeFile = file(keyStoreProperties["STORE_FILE"].toString())
            storePassword = keyStoreProperties["STORE_PASSWORD"].toString()
            keyAlias = keyStoreProperties["KEY_ALIAS"].toString()
            keyPassword = keyStoreProperties["KEY_PASSWORD"].toString()
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("releaseConfig")
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            buildConfigField("boolean", "IS_DEBUG", "true")
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("releaseConfig")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            buildConfigField("boolean", "IS_DEBUG", "false")
        }
    }

    // 将 release APK 复制到输出目录
    tasks.register("copyReleaseToOutputDir") {
        doLast {
            val outputDir = file("${project.rootDir}/app/release/")

            // 清理旧文件
            if (outputDir.exists()) {
                outputDir.deleteRecursively()
            }
            outputDir.mkdirs()

            // 确保 release APK 文件存在并复制
            applicationVariants.all {
                if (buildType.name == "release") {
                    outputs.forEach { output ->
                        val outputFile = output.outputFile
                        if (outputFile.exists()) {
                            val newApkName = "${autoConfig["APP_NAME"]}_${buildType.name}_v${versionName}.apk"
                            val destinationFile = outputDir.resolve(newApkName)
                            outputFile.copyTo(destinationFile, overwrite = true)
                            println("Copied ${outputFile.name} to $destinationFile")

                            if (outputFile.name.endsWith(".apk")) {
                                // 删除旧文件
                                outputFile.delete()
                            }
                        }
                    }
                }
            }
        }
    }
    applicationVariants.all {
        if (buildType.name == "release") {
            tasks.named("assemble${name.replaceFirstChar { it.uppercase() }}") {
                finalizedBy(tasks.named("copyReleaseToOutputDir"))
            }
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
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:ui"))
    implementation(project(":feature:splash"))
    implementation(project(":feature:login"))
    implementation(project(":feature:home"))
    implementation(project(":feature:im"))
    implementation(project(":feature:profile"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    testImplementation(libs.junit.jupiter.api)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // hilt不能引入模块使用，在需要使用hilt的模块单独引入依赖
    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.android.compiler)

    implementation(libs.androidx.profileinstaller)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.kotlinx.datetime)
}