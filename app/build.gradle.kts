plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.firebaserealtapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.firebaserealtapplication"
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
    buildFeatures{
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    sourceSets {
        getByName("main") {
            java {
                srcDirs("src/main/java", "src/main/java/2")
            }
        }
    }
}
dependencies {
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.activity)

    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.github.TutorialsAndroid:GButton:v1.0.19")
    implementation("com.google.android.gms:play-services-auth:20.4.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.core:core-ktx:1.10.1")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // YouTube Video Player API
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
}


//dependencies {
//
//    implementation(libs.appcompat)
//    implementation(libs.material)
//    implementation(libs.activity)
//    implementation(libs.constraintlayout)
//    implementation(libs.firebase.database)
//    implementation(libs.firebase.storage)
//    implementation(libs.firebase.auth)
//    implementation(libs.firebase.firestore)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.ext.junit)
//    androidTestImplementation(libs.espresso.core)
//    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
//    implementation ("com.github.TutorialsAndroid:GButton:v1.0.19")
//    implementation ("com.google.android.gms:play-services-auth:20.4.0")
//    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
//    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
//    implementation("com.google.android.material:material:1.9.0")
//    implementation ("com.github.bumptech.glide:glide:4.16.0")
//    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("androidx.core:core-ktx:1.10.1")
//    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
//    //you tube video play api
//    implementation ("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
//
//}