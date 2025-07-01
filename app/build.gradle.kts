plugins {
    alias(libs.plugins.android.application)
}

apply(plugin = "com.google.gms.google-services")

android {
    namespace = "com.example.parcial_1_am_acn4av_chiquilito_gomezpacheco"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.parcial_1_am_acn4av_chiquilito_gomezpacheco"
        minSdk = 24
        targetSdk = 35
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.code.gson:gson:2.10.1")

    // ✅ Firebase Bill of Materials (BoM)
    // Usa la versión más reciente que desees (32.8.1 es una buena referencia)
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))

    // ✅ Dependencias de Firebase - ya no necesitas especificar la versión aquí
    // La versión la manejará la BoM. Usamos las versiones -ktx para Kotlin.
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
}