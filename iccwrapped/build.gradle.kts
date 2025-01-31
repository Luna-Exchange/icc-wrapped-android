plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.parcelize)
    kotlin("kapt")
}

android {
    namespace = "com.icc.iccwrapped"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    api (libs.androidx.activity.ktx)
    api (libs.kotlinx.coroutines.core)
    api (libs.kotlinx.coroutines.android)
    api (libs.hilt.android)
    kapt (libs.hilt.compiler)
    api (libs.androidx.hilt.lifecycle.viewmodel)
    kapt (libs.androidx.hilt.compiler)
    api (libs.retrofit)
    api (libs.converter.gson)
    api (libs.okhttp)
    api (libs.androidx.lifecycle.viewmodel.ktx)
    api (libs.retrofit2.kotlin.coroutines.adapter)
    api (libs.logging.interceptor)
    api (libs.timber)
}