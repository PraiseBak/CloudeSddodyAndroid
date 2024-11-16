package com.study.sddodyandroid.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.MapsInitializer
import com.study.sddodyandroid.common.CheckedEnumViewModel
import com.study.sddodyandroid.component.start.StartNavHost


class StartActivity : ComponentActivity() {
    private val checkedEnumViewModel: CheckedEnumViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isModifyUser = intent.getBooleanExtra("isModifyUser",false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        MapsInitializer.initialize(this)

        setContent {
            /**
             * 회원수정을 하는 경우
             * 1. 뷰모델에 회원정보를 가져온뒤에 유저정보를 수정할 수 있음
             * 2.
             */

            StartNavHost(context = this, viewModel = checkedEnumViewModel, fusedLocationProviderClient = fusedLocationClient,
                isModifyUser = isModifyUser)
        }

    }
}

