package com.study.sddodyandroid.component.study

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.component.start.GoogleMapView
import com.study.sddodyandroid.dto.StudyResponseDto
import com.study.sddodyandroid.service.getStudyLocationInfoList

@Composable
fun RecommendStudyMap(
    navController: NavHostController,
    studyLocationList: MutableState<MutableList<StudyResponseDto>>
) {
    val context = LocalContext.current

    getStudyLocationInfoList(context,studyLocationList,true)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
        defaultElevation = 10.dp
    )) {

        GoogleMapView(
            fusedLocationClient = null,
            rememberStudyLocationList = studyLocationList,
            onMarkerClick = { id ->
                navController.navigate("study/$id")
//            false
            },
            isSignup = false,
            isOnlyNearRecommend = true
        )
    }


}