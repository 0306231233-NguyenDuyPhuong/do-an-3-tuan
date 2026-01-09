plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
<<<<<<< HEAD
=======
    // Sửa lại version phù hợp với Kotlin của bạn (thử 2.0.21 hoặc 2.1.0)
    kotlin("plugin.serialization") version "2.0.21"
>>>>>>> 189572c2eaf776e904c413aecbd5271a3dd0a8bb
}

android {
    namespace = "com.example.ui_doan3tuan"
<<<<<<< HEAD
    compileSdk = 36
=======
    compileSdk = 36 // Hạ xuống 35 để ổn định
>>>>>>> 189572c2eaf776e904c413aecbd5271a3dd0a8bb

    defaultConfig {
        applicationId = "com.example.ui_doan3tuan"
        minSdk = 27
<<<<<<< HEAD
        targetSdk = 36
=======
        targetSdk = 35 // Hạ xuống 35
>>>>>>> 189572c2eaf776e904c413aecbd5271a3dd0a8bb
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
<<<<<<< HEAD
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
=======

    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11 // Chuyển về 11 hoặc 17 để ổn định hơn nếu 21 lỗi
>>>>>>> 189572c2eaf776e904c413aecbd5271a3dd0a8bb
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
<<<<<<< HEAD

=======
>>>>>>> 189572c2eaf776e904c413aecbd5271a3dd0a8bb
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
<<<<<<< HEAD
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
=======
    implementation(libs.androidx.recyclerview)

    // OkHttp & Serialization
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // UI & Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("io.coil-kt:coil:2.6.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.code.gson:gson:2.10.1")
>>>>>>> 189572c2eaf776e904c413aecbd5271a3dd0a8bb
}