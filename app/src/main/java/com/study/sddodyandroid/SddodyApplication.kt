package com.study.sddodyandroid;


import android.app.Application
import android.content.pm.PackageManager
import com.google.firebase.FirebaseApp
import com.kakao.sdk.common.KakaoSdk
import com.study.sddodyandroid.common.RetrofitInstance
import com.study.sddodyandroid.service.ChatService

class SddodyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val NATIVE_APP_KEY = this.resources.getString(R.string.NATIVE_APP_KEY)
        KakaoSdk.init(this, NATIVE_APP_KEY)
        FirebaseApp.initializeApp(this);

        // Retrofit 초기화
        RetrofitInstance.initialize(this)

        val appInfo = this.packageManager.getPackageInfo(this.packageName,PackageManager.GET_META_DATA)
        val metaData = appInfo.applicationInfo.metaData

        ChatService.setSubscribeInfoList()
    }
}