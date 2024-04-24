plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.app.linkedlisting"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.app.linkedlisting"
        minSdk = 29
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database")
    implementation ("com.google.firebase:firebase-auth:22.3.1")

    implementation ("com.google.firebase:firebase-firestore:24.1.0")
    implementation ("com.google.android.material:material:1.4.0")
    implementation ("com.google.firebase:firebase-storage:20.0.0")
    implementation ("com.google.firebase:firebase-appcheck-safetynet:16.0.0-beta01")

    implementation ("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.0")


    testImplementation("junit:junit:4.13.2")


    // Unit Tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.0.0")
    testImplementation("org.robolectric:robolectric:4.6")
    testImplementation ("androidx.arch.core:core-testing:2.1.0")

// Android Instrumented Tests
    testImplementation("androidx.test:core:1.2.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.fragment:fragment-testing:1.4.0")
    androidTestImplementation("org.mockito:mockito-android:4.0.0")


}