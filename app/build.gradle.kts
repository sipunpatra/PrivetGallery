plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")
}

android {
    namespace = "gov.orsac.hideapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "gov.orsac.hideapp"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding =true
    }
}

dependencies {
    val room_version = "2.6.1"
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")


    //loti
    implementation ("com.airbnb.android:lottie:6.0.0")

    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    kapt ("androidx.room:room-compiler:$room_version")
    androidTestImplementation ("androidx.room:room-testing:$rootProject.roomVersion")

    implementation ("com.google.android.exoplayer:exoplayer:2.19.1")
//    implementation ("androidx.media3:media3-exoplayer:1.7.1")
//    implementation ("androidx.media3:media3-ui:1.7.1")




}