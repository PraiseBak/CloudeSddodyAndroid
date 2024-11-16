package com.study.sddodyandroid.component.start

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.study.sddodyandroid.R
import com.study.sddodyandroid.common.CheckedEnumViewModel
import com.study.sddodyandroid.dto.LocationDto
import com.study.sddodyandroid.dto.MemberInfoDto
import com.study.sddodyandroid.dto.StudyResponseDto
import com.study.sddodyandroid.helper.CheckAndRequestGPSPermission
import com.study.sddodyandroid.service.getMemberInfo
import com.study.sddodyandroid.ui.component.main.study.StudyCard


fun checkLocationServiceEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

@Composable
fun GoogleMapViewBySpecificLocation(
    location : LatLng,
){
    GoogleMap(
        modifier = Modifier
            .fillMaxSize()
            .height(300.dp)
        ,
        cameraPositionState = CameraPositionState(
            position = CameraPosition.fromLatLngZoom(location, 16f)
        )
    ){
        Marker(
            state = MarkerState(position = location),
        )

    }

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GoogleMapView(
    fusedLocationClient: FusedLocationProviderClient? = null,
    searchResultList: MutableList<LocationDto> = mutableListOf(),
    cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.3846, 127.1250), 14f)
    },
    viewModel: CheckedEnumViewModel? = null,
    rememberStudyLocationList: MutableState<MutableList<StudyResponseDto>>?,
    onMarkerClick: (Long) -> Unit,
    isSignup: Boolean = false,
    isOnlyNearRecommend: Boolean
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var initLocation : MutableState<LatLng?> = remember { mutableStateOf(cameraPositionState.position.target) }
    var initRequestPermission by remember { mutableStateOf(true) }

    if(rememberStudyLocationList?.value?.isEmpty() == true){
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("현재 참여할 수 있는 스터디가 없어요")
        }
    }


    if(isSignup && initRequestPermission && fusedLocationClient != null){
        GetInitLocation(cameraPositionState,fusedLocationClient,initLocation,viewModel)
        initRequestPermission = false
    }

    //회원가입인경우
    if(isSignup){
        LaunchedEffect(cameraPositionState.position) {
            viewModel?.curLocation = cameraPositionState.position.target
        }
        GoogleMap(
            modifier = Modifier
                .fillMaxSize(),
            cameraPositionState = cameraPositionState,
        ){
            Marker(
                state = MarkerState(position = cameraPositionState.position.target)
            )
        }
    }else{
        val sharedPreferences = context.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE)

        val rememberMemberInfo : MutableState<MemberInfoDto?> = remember{ mutableStateOf(null) }
        getMemberInfo(context,rememberMemberInfo,0)

        //스터디 맵 뷰
        rememberMemberInfo.value?.location?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 17f)
        }

        cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(36.3846, 127.1250), 17f)
        GoogleMapWithStudyMarker(context,cameraPositionState,rememberStudyLocationList,onMarkerClick,rememberMemberInfo)
    }



}

@Composable
@SuppressLint("MissingPermission")
fun GetInitLocation(cameraPositionState: CameraPositionState, fusedLocationClient: FusedLocationProviderClient, initLocation: MutableState<LatLng?>, viewModel: CheckedEnumViewModel?) {


    //curLocation 가져오기
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
                initLocation.value = curLocation
                if(viewModel != null){
                    viewModel.curLocation= initLocation.value as LatLng
                }
                println("위치 가져오는데 성공")
            } ?: {
                println("위치 가져오는데 실패")
            }
        }
    }

    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    CheckAndRequestGPSPermission(fusedLocationClient)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GoogleMapWithStudyMarker(
    context: Context,
    cameraPositionState: CameraPositionState,
    rememberStudyLocationList: MutableState<MutableList<StudyResponseDto>>?,
    onMarkerClick: (Long) -> Unit,
    rememberMemberInfo: MutableState<MemberInfoDto?>
) {
//    var bitmap :MutableState<Bitmap?> = remember{ mutableStateOf(null)}
    var isLoad = remember{ mutableStateOf(false) }
    val accessToken = context.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE).getString("accessToken","")
    val refreshToken = context.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE).getString("refreshToken","")

    val iconList = remember{ mutableStateListOf<Bitmap>()}

    rememberMemberInfo.value?.location?.let {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 17f)
    }


    rememberStudyLocationList?.value?.forEach{
        var imageSrc = "default_study.png"

        if(it.studyImageSrcList.isNotEmpty()){
            imageSrc = it.studyImageSrcList[0]
        }
    }
    isLoad.value = true


    if(isLoad.value){
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
        ){
            rememberStudyLocationList?.value?.forEachIndexed{index,it ->
                val id = it.studyId
                val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.book3)
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 180, 120, true)
                val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

                MarkerInfoWindow(
                    state = MarkerState(position = it.location),
                    title = it.title,
                    snippet = it.content,
                    icon = bitmapDescriptor,
                    onInfoWindowClick = {
                        onMarkerClick(id)
                    },
                ) { marker ->
                    // Implement the custom info window here
                    Column {
                        StudyCard(
                            it,
                            navController = rememberNavController(),
                            modifier = Modifier,
                            {false},
                            isPreview = true
                        )

                    }
                }
            }
        }



    }
}