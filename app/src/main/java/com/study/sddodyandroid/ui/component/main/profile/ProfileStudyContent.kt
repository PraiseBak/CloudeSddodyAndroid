package com.study.sddodyandroid.ui.component.main.profile

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.dto.PageDto
import com.study.sddodyandroid.dto.StudyResponseDto
import com.study.sddodyandroid.service.getMemberStudyList
import com.study.sddodyandroid.ui.component.study.EmptyComingSoon
import com.study.sddodyandroid.ui.component.main.study.StudyCard


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnrememberedMutableState")
@Composable
fun PartOfStudyContent(navController: NavHostController, memberId: Long) {
    var rememberStudyContent : MutableState<MutableList<StudyResponseDto>> = remember { mutableStateOf(mutableListOf()) }
    val rememberIsLoading = remember{ mutableStateOf(false) }
    val context = LocalContext.current

    getMemberStudyList(memberId = memberId,context,rememberStudyContent,rememberIsLoading)

    if(rememberStudyContent.value.size == 0){
        EmptyComingSoon("가입한 스터디가 없어요")
    }else{
        ProfileStudyList(
            studyList = rememberStudyContent, navController = navController,
            onClick = { studyId -> navController.navigate("study/$studyId") },
        )
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileStudyList(
    studyList: MutableState<MutableList<StudyResponseDto>>,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onClick: (Long) -> Unit,
) {

    studyList.value.forEach{
        StudyCard(
            study = it,
            navController = navController,
            onClick = onClick,
            isPreview = false
        )

        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }


//    Box(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
//
//        studyList.value.forEach{
//            StudyCard(
//                study = it,
//                navController = navController,
//                onClick = onClick,
//                isPreview = false
//            )
//
//            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
//
//        }

//        LazyColumn(
//            modifier = modifier
//                .fillMaxWidth(),
//        ) {
//            items(studyList.value) {
//                StudyCard(
//                    study = it,
//                    navController = navController,
//                    onClick = onClick,
//                    isPreview = false
//                )
//            }
//            // Add extra spacing at the bottom if
//            item {
//                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
//            }
//        }
//    }
}