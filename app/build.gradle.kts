plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "edu.oregonstate.cs492.githubsearch"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "edu.oregonstate.cs492.githubsearch"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.core.ktx)

    implementation(libs.material)

    implementation(libs.retrofit2.retrofit)
    implementation(libs.retrofit2.converterscalars)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}