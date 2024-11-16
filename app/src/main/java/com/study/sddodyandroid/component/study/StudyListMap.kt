package com.study.sddodyandroid.component.study

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.android.gms.location.FusedLocationProviderClient
import com.study.sddodyandroid.component.start.SetLocation
import com.study.sddodyandroid.dto.StudyResponseDto
import com.study.sddodyandroid.service.getStudyLocationInfoList

//전체 스터디 맵
@Composable
fun StudyListMap(navController: NavHostController, fusedLocationClient: FusedLocationProviderClient,modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val studyLocationList : MutableList<StudyResponseDto> = mutableListOf()
    val rememberStudyLocationList = remember{ mutableStateOf(studyLocationList) }
    
    getStudyLocationInfoList(context,rememberStudyLocationList)
    SetLocation(navController = navController, fusedLocationProviderClient = fusedLocationClient,rememberStudyLocationList = rememberStudyLocationList, modifier = modifier)

}