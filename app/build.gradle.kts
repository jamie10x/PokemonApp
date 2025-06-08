plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.jamie.pokedexhiltversion"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jamie.pokedexhiltversion"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner = "com.jamie.pokedexhiltversion.HiltTestRunner"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
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
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Jetpack Compose Material Icons
    implementation("androidx.compose.material:material-icons-core:1.7.8")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    // Lifecycle ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.9.0")

    // ConstraintLayout Compose
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // Lifecycle KTX
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1")

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Palette
    implementation("androidx.palette:palette:1.0.0")

    // Compose UI Text Google Fonts
    implementation("androidx.compose.ui:ui-text-google-fonts:1.8.2")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.56.2")
    // Replace kapt with ksp
    ksp("com.google.dagger:hilt-android-compiler:2.56.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Room
    implementation("androidx.room:room-runtime:2.7.1")
    // Replace kapt with ksp
    ksp("androidx.room:room-compiler:2.7.1")
    implementation("androidx.room:room-ktx:2.7.1")
    implementation("androidx.room:room-paging:2.7.1")

    // Paging 3
    implementation("androidx.paging:paging-runtime-ktx:3.3.6")
    implementation("androidx.paging:paging-compose:3.3.6")
    implementation("com.valentinilk.shimmer:compose-shimmer:1.2.0")

    testImplementation(libs.junit)

    // Truth for fluent assertions
    testImplementation("com.google.truth:truth:1.4.2")

    // Mockito for mocking objects
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")

    // Coroutines testing
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

    // For testing LiveData and ViewModel
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.56.2")
    kspAndroidTest("com.google.dagger:hilt-android-compiler:2.56.1")

    // For asserting on navigation
    androidTestImplementation("androidx.navigation:navigation-testing:2.9.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")

}