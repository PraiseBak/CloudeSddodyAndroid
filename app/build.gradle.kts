import com.google.api.AnnotationsProto.http
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())

android {
    namespace = "com.study.sddodyandroid"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.study.sddodyandroid"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildTypes{
            debug{
//                resValue("string", "SERVER_IP", "http://10.0.2.2:8080")


                resValue("string", "SERVER_IP", "http://54.180.116.170:8080")
            }
            release {
                resValue("string", "SERVER_IP", "http://54.180.116.170:8080")
            }
        }
        resValue("string", "MAPS_API_KEY", properties.getProperty("MAPS_API_KEY"))
        resValue("string", "NATIVE_APP_KEY", properties.getProperty("NATIVE_APP_KEY"))
        resValue("string", "GITHUB_CLIENT_ID", properties.getProperty("GITHUB_CLIENT_ID"))
        resValue("string", "GITHUB_CLIENT_SECRET", properties.getProperty("GITHUB_CLIENT_SECRET"))
        resValue("string", "KAKAO_SDK", properties.getProperty("KAKAO_SDK"))
    }

//    buildTypes {
//        release {
//            isMinifyEnabled = false
////            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
//        }
//    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19

    }
    kotlinOptions {
        jvmTarget = "19"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "34.0.0"
}


dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.7.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("com.google.firebase:firebase-inappmessaging-ktx:20.4.0")
    implementation("com.google.firebase:firebase-messaging-ktx:23.3.1")
    implementation("androidx.window:window:1.2.0")
    implementation("androidx.media3:media3-common:1.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("com.kakao.sdk:v2-all:2.18.0") // 전체 모듈 설치, 2.11.0 버전부터 지원
    implementation("com.kakao.sdk:v2-user:2.18.0") // 카카오 로그인
    implementation("com.kakao.sdk:v2-talk:2.18.0") // 친구, 메시지(카카오톡)
    implementation("com.kakao.sdk:v2-share:2.18.0") // 메시지(카카오톡 공유)
    implementation("com.kakao.sdk:v2-friend:2.18.0") // 카카오톡 소셜 피커, 리소스 번들 파일 포함
    implementation("com.kakao.sdk:v2-cert:2.18.0") // 카카오 인증서비스
    implementation(platform("com.google.firebase:firebase-bom:32.6.0")) //파이어베이스
    implementation("com.google.firebase:firebase-analytics")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    implementation("androidx.navigation:navigation-compose:2.7.6")

    //mui
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.material3:material3-window-size-class:1.1.2")
    implementation("androidx.compose.material3:material3-adaptive:1.0.0-alpha04")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.0.0-alpha02")

    implementation("com.google.maps.android:maps-compose:2.11.4")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.0.7")
    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("com.google.accompanist:accompanist-adaptive:0.26.2-beta")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    //viewPager
    implementation("com.google.accompanist:accompanist-pager:0.20.1")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.20.1")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("me.onebone:toolbar-compose:2.3.5")
    implementation("io.coil-kt:coil-svg:2.6.0")

    //openid auth
    implementation("net.openid:appauth:0.11.1")

}
