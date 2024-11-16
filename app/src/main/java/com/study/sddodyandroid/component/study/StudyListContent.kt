package com.study.sddodyandroid.component.study

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.dto.PageDto
import com.study.sddodyandroid.dto.StudyResponseDto
import com.study.sddodyandroid.helper.study.StudyOrderType
import com.study.sddodyandroid.service.getStudyList
import com.study.sddodyandroid.ui.component.main.study.StudyCard
import com.study.sddodyandroid.ui.component.study.EmptyComingSoon


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnrememberedMutableState", "MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyView(
    isPreview: Boolean = false,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    orderType: MutableState<StudyOrderType>,
) {
    EmptyComingSoon("아직 작성된 게시물이 없어요")

    val rememberPaging = remember{ mutableStateOf(PageDto(page = 1)) }
    var rememberStudyContent : MutableState<MutableList<StudyResponseDto>> = remember { mutableStateOf(mutableListOf()) }

    val rememberIsLoading = remember{ mutableStateOf(true) }

    val context = LocalContext.current

    Column(modifier = Modifier) {

        LaunchedEffect(rememberPaging,orderType.value){
            getStudyList(pageDto = rememberPaging.value,context,rememberStudyContent,rememberIsLoading,orderType.value)
        }

        if(rememberIsLoading.value){
            Box(modifier = Modifier
                .background(color = Color.White)
            ) {
                StudyList(
                    isPreview = isPreview,
                    studyList = rememberStudyContent,
                    navController = navController,
                    onClick = { studyId -> navController.navigate("study/$studyId")},
                    modifier = modifier
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudyList(
    isPreview: Boolean,
    studyList: MutableState<MutableList<StudyResponseDto>>,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onClick : (Long) -> Unit
) {

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ){

        items(studyList.value){
            StudyCard(
                study = it,
                navController = navController,
                onClick = onClick,
                isPreview = false
            )
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
        }

        if(studyList.value.isEmpty()){
            item {
                Column(modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EmptyComingSoon("아직 참여 가능한 스터디가 없어요")
                }
            }
        }
    }
}