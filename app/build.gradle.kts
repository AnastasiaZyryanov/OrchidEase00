plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
  //  alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

println("DEBUG: SUPABASE_KEY = " + project.findProperty("SUPABASE_KEY"))

android {
    namespace = "com.example.orchidease00"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.orchidease00"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        renderscriptTargetApi = 21
        renderscriptSupportModeEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val supabaseKey: String? = project.findProperty("SUPABASE_KEY") as String?

        if (supabaseKey != null) {
            buildConfigField("String", "SUPABASE_KEY", "\"$supabaseKey\"")
        } else {
            throw GradleException("SUPABASE_KEY is not defined in local.properties")

        }

        buildConfigField("String", "SUPABASE_KEY", "\"${project.findProperty("SUPABASE_KEY")}\"")


        compileOptions {
            isCoreLibraryDesugaringEnabled = true
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
            //sourceCompatibility = JavaVersion.VERSION_11
           // targetCompatibility = JavaVersion.VERSION_11
        }
        kotlinOptions {
           // jvmTarget = "11"
            jvmTarget = "1.8"
        }
        buildFeatures {
            compose = true
            buildConfig = true

        }

        composeOptions {
            kotlinCompilerExtensionVersion = "1.5.10"
        }
        packagingOptions {
            resources.excludes.add("META-INF/*")
        }

        sourceSets {
            getByName("main").java.srcDir("build/generated/ksp/main/kotlin")
        }

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }



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
}


dependencies {

    // Compose и Material
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material3:material3:1.2.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    ksp("androidx.room:room-compiler:2.6.1")


    // Core и Activity
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    //implementation("androidx.compose.foundation:foundation:1.6.1")
    implementation(libs.androidx.foundation)
    implementation("androidx.compose.foundation:foundation-layout:1.6.1")
    implementation("androidx.compose.ui:ui:1.6.1")
    implementation(libs.coil.compose)
    implementation ("androidx.compose.ui:ui-graphics")

    implementation(libs.androidx.material.icons)

    coreLibraryDesugaring(libs.desugarJdkLibs)

}