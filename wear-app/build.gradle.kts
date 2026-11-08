plugins {
    kotlin("android")
    id("com.android.application")
}

android {
    namespace = "com.wristpad.wear"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.wristpad.wear"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.ui:ui-graphics:1.5.0")
    implementation("androidx.wear:wear-compose:1.3.0")
    implementation("androidx.wear:wear:1.3.0")
    implementation("androidx.compose.material:material-icons-core:1.5.0")
    // Wear Data Layer
    implementation("androidx.wear:wearable:1.3.0")
}