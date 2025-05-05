plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.npconfessions"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.npconfessions"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    // AndroidX / Material
    implementation(libs.core)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)

    // Room (SQLite)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)   // ⇦ Java build; use kapt(...) if you switch to Kotlin

    // Retrofit + Gson
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)


    // WorkManager
    implementation(libs.work.runtime)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.preference)

    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
}
