package com.study.sddodyandroid.component.main.createstudy

import android.annotation.SuppressLint
import android.os.Looper
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.study.sddodyandroid.dto.LocationDto
import com.study.sddodyandroid.helper.CheckAndRequestGPSPermission
import com.study.sddodyandroid.common.CreateStudyViewModel

@SuppressLint("MissingPermission")
@Composable
fun StudyGoogleMapView(
    fusedLocationClient: FusedLocationProviderClient,
    searchResultList: MutableList<LocationDto>,
    cameraPositionState: CameraPositionState,
    viewModel: CreateStudyViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var initLocation : LatLng? by remember { mutableStateOf(null) }
    var initRequestPermission by remember { mutableStateOf(true) }


    Column (
        modifier = Modifier
    ){
        // 위치 정보 요청


        if(initRequestPermission){
            CheckAndRequestGPSPermission(fusedLocationClient)
            initRequestPermission = false
        }

        if(initLocation == null && !initRequestPermission){
            val locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    p0.lastLocation?.let { location ->
                        // 위치 정보를 성공적으로 얻었을 때 실행되는 부분
                        val latitude = location.latitude
                        val longitude = location.longitude
                        val curLocation = LatLng(latitude,longitude)
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(curLocation, 17f)

                        // 위치 업데이트 중지
                        fusedLocationClient.removeLocationUpdates(this)
                        initLocation = curLocation
                        viewModel.onLocation(curLocation)
                    } ?: {

                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
        LaunchedEffect(cameraPositionState.position) {
            viewModel.onLocation(cameraPositionState.position.target)
        }


        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ){
            Marker(
                state = MarkerState(position = cameraPositionState.position.target)
            )
        }
    }



}