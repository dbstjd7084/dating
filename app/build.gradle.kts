plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.dbsthd2459.datingapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dbsthd2459.datingapp"
        minSdk = 31
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    // 충돌 파일 제외
    packagingOptions {
        exclude("META-INF/INDEX.LIST")
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/*.kotlin_module")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.work.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // CardStackView
    implementation("com.github.yuyakaido:cardstackview:2.3.4")
    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    // Firebase Auth / 계정
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    // Firebase Storage / 프로필 이미지 저장용
    implementation("com.google.firebase:firebase-storage")
    // Firebase 프로필 이미지 가져오기용 Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Firebase Cloud Messaging / 푸시 메시지용
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.30.1")
    implementation("org.tensorflow:tensorflow-lite:2.5.0")
}