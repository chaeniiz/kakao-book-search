plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.chaeniiz.domain"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":entity"))

    // Dependency Injection
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.android)
    
    // Test
    testImplementation(libs.junit)
}
