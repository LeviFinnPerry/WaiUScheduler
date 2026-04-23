plugins {
    alias(libs.plugins.android.application)
    // Add this if you have the google-services plugin in your TOML
    // alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.waiuscheduler"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.waiuscheduler"
        minSdk = 35
        targetSdk = 36
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

    buildFeatures {
        viewBinding = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        // CRITICAL: Tells the Kotlin DSL to use the JUnit 5 engine
        unitTests.all {
            it.useJUnitPlatform()
        }
    }

    sourceSets {
        getByName("test") {
            resources.srcDirs("src/test/java")
        }
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md"
            )
        }
    }
}

dependencies {
    // Core Android
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)

    // Architecture Components
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.extensions)
    annotationProcessor(libs.lifecycle.compiler)

    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Firebase
    implementation(libs.firebase.firestore)

    // Unit Testing (JUnit 5)
    testImplementation(libs.junit.jupiter)
    // Manually adding the engine to ensure it's present for discovery
    testRuntimeOnly(libs.junit.jupiter.engine)

    // Instrumented Testing
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.instant.task)

    // Database (Room)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // JSON and Networking
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    // HTML Scraping
    implementation(libs.jsoup)
}
