plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "pi.biblioteca"
    compileSdk = 34

    defaultConfig {
        applicationId = "pi.biblioteca"
        minSdk = 30
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
}

dependencies {
    // Implementación de OpenCV
    implementation(project(":opencv"))

    // Implementación de tesseract To use Standard variant:
    implementation("cz.adaptech.tesseract4android:tesseract4android:4.7.0")

    // To use OpenMP variant:
    //implementation("cz.adaptech.tesseract4android:tesseract4android-openmp:4.7.0")

    // Implementación Google MLKit. To recognize Latin script
    implementation("com.google.mlkit:text-recognition:16.0.1")

    // Implementacion LenguajeTool
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")

    // Implementación para usar Google Books
    implementation("com.android.volley:volley:1.2.1")

    // Implementación para el comparador de textos
    implementation("org.apache.commons:commons-text:1.9")

    // Implementación para el escaneo de código
    implementation("com.google.android.gms:play-services-code-scanner:16.1.0")

    // Implementación para la base de datos
    implementation("androidx.room:room-runtime:2.5.0")
    annotationProcessor("androidx.room:room-compiler:2.5.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.camera.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}