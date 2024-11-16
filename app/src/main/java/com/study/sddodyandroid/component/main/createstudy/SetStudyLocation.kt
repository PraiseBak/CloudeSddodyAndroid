package com.study.sddodyandroid.component.main.createstudy

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.study.sddodyandroid.common.CreateStudyViewModel
import com.study.sddodyandroid.component.start.SearchBar
import com.study.sddodyandroid.dto.LocationDto
import com.study.sddodyandroid.helper.getSearchResult
import com.study.sddodyandroid.ui.component.main.create.QuestionWrapper
import kotlinx.coroutines.launch


@Composable
fun SetLocation(
    @StringRes titleResourceId: Int,
    location: MutableState<LatLng>,
    viewModel: CreateStudyViewModel,
    fusedLocationProviderClient: FusedLocationProviderClient,
    modifier: Modifier
){
    QuestionWrapper(
        titleResourceId = titleResourceId,
        modifier = modifier,
    ){
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        var searchFieldText by remember { mutableStateOf(TextFieldValue("")) }
        var searchBarFocus by remember { mutableStateOf(false) }
        val searchResultList = remember { mutableStateListOf<LocationDto>()}
        val selectLocation = remember { mutableStateOf<LocationDto?>(null) }
        var curLocation = LatLng(37.3846, 127.1250)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(curLocation, 16f)
        }
        val keyboardController = LocalSoftwareKeyboardController.current


        val focusManager: FocusManager = LocalFocusManager.current
        // 상단 섹션

        Column {
            Text(text = "스터디가 진행될 장소를 선택해주세요")
        }


        // 중간 섹션 (검색 바와 지도)
        Column(
            modifier = Modifier.fillMaxSize(1f)
        ) {
            Box {
                SearchBar(
                    focus = searchBarFocus,
                    onFocus = { searchBarFocus = it },
                    onSearch = {
                        lifecycleOwner.lifecycleScope.launch {
                            try {
                                getSearchResult(
                                    context = context,
                                    inputText = it,
                                    searchResultList = searchResultList
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    },
                    searchResultList = searchResultList,
                    selectLocation = selectLocation,
                    cameraPositionState = cameraPositionState,
                )

                if (!searchBarFocus) {
                    StudyGoogleMapView(fusedLocationClient = fusedLocationProviderClient,searchResultList,cameraPositionState, viewModel)
                }
            }
        }

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetStudyLocation(
    location: MutableState<LatLng>,
    viewModel: CreateStudyViewModel,
    fusedLocationProviderClient: FusedLocationProviderClient,
    modifier: Modifier
) {
    Log.d("location", "SetStudyLocation화면")
    Column(modifier.fillMaxSize()) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        var searchFieldText by remember { mutableStateOf(TextFieldValue("")) }
        var searchBarFocus by remember { mutableStateOf(false) }
        val searchResultList = remember { mutableStateListOf<LocationDto>() }
        val selectLocation = remember { mutableStateOf<LocationDto?>(null) }
        var curLocation = LatLng(37.3846, 127.1250)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(curLocation, 16f)
        }
        val keyboardController = LocalSoftwareKeyboardController.current

        val focusManager: FocusManager = LocalFocusManager.current
        // 상단 섹션

        // 중간 섹션 (검색 바와 지도)
        Column(modifier.weight(3f)) {
            Box {

                SearchBar(
                    focus = searchBarFocus,
                    onFocus = { searchBarFocus = it },
                    onSearch = {
                        lifecycleOwner.lifecycleScope.launch {
                            try {
                                getSearchResult(
                                    context = context,
                                    inputText = it,
                                    searchResultList = searchResultList
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    },
                    searchResultList = searchResultList,
                    selectLocation = selectLocation,
                    cameraPositionState = cameraPositionState,
                )

                if (!searchBarFocus) {
                    StudyGoogleMapView(
                        fusedLocationClient = fusedLocationProviderClient,
                        searchResultList,
                        cameraPositionState,
                        viewModel
                    )
                }
            }
        }

//        Column(Modifier.weight(0.6f)) {
//            BottomButtonRow(
//                curType = "language",
//                nextType = "developer",
//                navController = navController,
//                viewModel = viewModel,
//                location = cameraPositionState.position.target
//            )
//        }


    }
}
}