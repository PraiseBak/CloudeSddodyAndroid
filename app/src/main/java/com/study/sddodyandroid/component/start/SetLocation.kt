package com.study.sddodyandroid.component.start

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.study.sddodyandroid.common.CheckedEnumViewModel
import com.study.sddodyandroid.dto.LocationDto
import com.study.sddodyandroid.dto.StudyResponseDto
import com.study.sddodyandroid.helper.getSearchResult
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetLocation(
    navController: NavHostController,
    viewModel: CheckedEnumViewModel? = null,
    fusedLocationProviderClient: FusedLocationProviderClient,
    rememberStudyLocationList: MutableState<MutableList<StudyResponseDto>>? = null,
    modifier: Modifier = Modifier,
    isSignup : Boolean = false
) {
    Column(modifier.fillMaxSize()) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        var searchFieldText by remember { mutableStateOf(TextFieldValue("")) }
        var searchBarFocus by remember { mutableStateOf(false) }
        val searchResultList = remember { mutableStateListOf<LocationDto>()}
        val selectLocation = remember { mutableStateOf<LocationDto?>(null) }
        var zoom = if(rememberStudyLocationList != null) 7f else 16f
        var curLocation = LatLng(37.3846, 127.1250)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(curLocation, zoom)
        }
        var keyboardController = LocalSoftwareKeyboardController.current
        val focusManager: FocusManager = LocalFocusManager.current

        Column(Modifier.weight(3f)) {
            if(isSignup){
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(50f),
                    text = "마지막으로 어디서 활동하시는지 알려주세요!",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Box {
                if(isSignup){
                    SearchBar(
                        focus = searchBarFocus,
                        onFocus = {
                            searchBarFocus = it;
                        },
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
                }

                if (!searchBarFocus) {
                    GoogleMapView(
                        fusedLocationClient = fusedLocationProviderClient, searchResultList,
                        cameraPositionState, viewModel, rememberStudyLocationList,
                        onMarkerClick = { id ->
                            navController.navigate("study/$id")
                        },
                        isSignup = isSignup,
                        true,
                    )
                }

            }

//            if(viewModel != null){
//                BottomButtonRow(
//                    curType = "language",
//                    nextType = "developer",
//                    navController = navController,
//                    viewModel = viewModel,
//                    location = cameraPositionState.position.target
//                )
//            }
        }
    }

}
