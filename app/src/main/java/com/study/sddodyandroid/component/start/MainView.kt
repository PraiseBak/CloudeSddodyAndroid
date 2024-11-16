package com.study.sddodyandroid.component.start

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.activity.MainViewRoute
import com.study.sddodyandroid.component.community.CommunityView
import com.study.sddodyandroid.component.study.RecommendStudyMap
import com.study.sddodyandroid.dto.MemberInfoDto
import com.study.sddodyandroid.dto.StudyResponseDto
import com.study.sddodyandroid.service.ChatService
import com.study.sddodyandroid.service.getMemberInfo
import com.study.sddodyandroid.service.setRecommendStudy
import com.study.sddodyandroid.ui.common.CommonButtonColor
import com.study.sddodyandroid.ui.component.main.study.StudyCard


@Composable
fun LogoText(text : String){
    Text(
        text,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(start = 20.dp)
    )
}

@Composable
fun MainText(text : String){
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(start = 8.dp)
    )
}




@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainView(navController: NavHostController) {
    //main들어올때마다 초기화해주기
    val kakaoLoginPreference = LocalContext.current.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE)
    val memberId = kakaoLoginPreference.getString("memberId","")
    ChatService.initMemberSubscribe("member_${memberId.toString()}")
    val studyLocationList : MutableList<StudyResponseDto> = mutableListOf()
    val rememberStudyLocationList = remember{ mutableStateOf(studyLocationList) }


        Box (
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn() {
                item{


                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
//                            .padding(horizontal = 10.dp)


                    ) {

                            RecommendStudy(navController)
                            Column(modifier = Modifier.padding(top = 10.dp)) {
                            }

                            Text("내 활동지역 주변 추천하는 스터디가 ${rememberStudyLocationList.value.size}개 있어요",
                                modifier = Modifier.padding(start = 20.dp))

                            Button(colors = CommonButtonColor(),
                                modifier = Modifier.padding(start = 20.dp),
                                onClick = {navController.navigate("study/map")}) {
                                Row(){
                                    Icon(imageVector = Icons.Default.Map, contentDescription = "지도 스터디")
                                    Text("진행되고 있는 스터디 보기",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 8.dp,top = 4.dp)
                                    )
                                }
                            }

                            Box(modifier = Modifier
                                .fillMaxSize()
                                .height(400.dp)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clip(shape = RoundedCornerShape(4.dp)) // 둥근 테두리 적용
                            ){
                                RecommendStudyMap(navController,rememberStudyLocationList)
                            }


                    }

                }


                item {

                    Column(modifier = Modifier.padding(top = 20.dp)) {

                    }

                    LogoText("커뮤니티")

                    Text(text = "다른 개발자들의 생각이에요",
                        Modifier
                            .alpha(0.5f)
                            .padding(start = 20.dp))

                    Button(colors = CommonButtonColor(),
                        modifier = Modifier.padding(start = 20.dp,top = 8.dp),
                        onClick = {navController.navigate(MainViewRoute.COMMUNITY_LIST)}) {
                        Text("다른 글 읽어보기",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }

                    Column(modifier=  Modifier.padding(8.dp)) {

                    }

                    CommunityView(navController = navController, true,
                        onChangeCommunityViewClick ={navController.navigate("community/list")})

                    Spacer(Modifier.height(64.dp))
                    HorizontalDivider()

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("문의 praisebak@naver.com")
                    }
                    Spacer(Modifier.height(32.dp))
                }

            }

            val fabModifier = Modifier
                .padding(bottom = 80.dp)
                .align(Alignment.BottomEnd)
            val modifier = Modifier.align(Alignment.BottomEnd)
//        바텀내비게이션바
//        CreateFloatingActionButton(navController = navController, modifier = fabModifier){
//            navController.navigate("selectedCreate")
//        }


    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RecommendStudy(navController: NavHostController) {
    var studyResponseDtoList : MutableList<StudyResponseDto> = mutableListOf()
    var rememberStudyResponseList = remember{ mutableStateOf(studyResponseDtoList) }

    setRecommendStudy(LocalContext.current, rememberStudyResponseList)
    val context = LocalContext.current
    val rememberMemberInfo : MutableState<MemberInfoDto?> = remember{ mutableStateOf(null) }
    getMemberInfo(context,rememberMemberInfo,0)

    var enumIndex = 0
    rememberMemberInfo.value?.devYearEnum?.yearStr?.let {
        if(it.contains("0-2")){
            enumIndex = 0
        }else{
            enumIndex = 1
        }
    }
    var showDialog by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row(){
            LogoText("추천 스터디")

            Icon(Icons.Default.Info, contentDescription = "설명", modifier = Modifier
                .padding(top = 6.dp, start = 6.dp)
                .clickable {
                    showDialog = true
                }
                .alpha(0.5f))
        }


        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(modifier = Modifier.padding(top = 8.dp)) {




                    /**
                     * 다음의 추천은 같은 직종을 선택한 스터디와 프레임워크를 기반으로 추천드리고 있어요
                     * 2023년 기준 프로그래머스 설문조사로 현재 개발자들은
                     * 스프링계열, js계열,리액트를 선호해요
                     * (경력자인 경우 36, 22, 27)
                     * (주니어인 경우 38, 19, 29)
                     */

                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "추천",
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            },
                            text = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = """
                다음의 추천은 스터디를 같은 직종,
                개발자들이 선호하는 프레임워크를 기반으로 추천드리고 있어요.
                
            """.trimIndent(),
                                        style = MaterialTheme.typography.bodyMedium,
                                    )

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "추천하는 프레임워크 기준은?",
                                            textAlign = TextAlign.Center,
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }
                                    Text(
                                        text = """
                                    2023년 기준(프로그래머스 설문조사 기반)
                                    현재 개발자들은 스프링계열, js계열, 리액트를 선호하는 경향을 보여요.
                                    (경력자인 경우 36, 22, 27)
                                    (주니어인 경우 38, 19, 29)
                                   
            """.trimIndent(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "다른 추천 기능은 없나요?",
                                            style = MaterialTheme.typography.titleMedium,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }

                                    Text(
                                        text = """
                                    위치,자신이 관심있는 프레임워크의 추천은 스터디에서 정렬기능을 사용할 수 있어요
            """.trimIndent(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = { showDialog = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                                ) {
                                    Text("닫기")
                                }
                            }
                        )
                    }
                }
        }

        if (rememberStudyResponseList.value.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("현재 참여할 수 있는 스터디가 없어요")
            }
        }

        LazyRow(){

            item {

                Column {
                    Button(colors = CommonButtonColor(),
                        modifier = Modifier.padding(start = 8.dp),
                        onClick = { navController.navigate(MainViewRoute.CREATE_STUDY) }) {
                        Row {
                            Icon(imageVector = Icons.Default.MenuBook, contentDescription = "스터디 리스트")
                            Text(
                                "간단하게 스터디 만들기",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                            )
                        }

                    }
                }
            }

            item{
                if (rememberStudyResponseList.value.size > 0) {
                    Column {
                        Row {
                            Button(colors = CommonButtonColor(),
                                modifier = Modifier.padding(start = 8.dp),
                                onClick = { navController.navigate(MainViewRoute.STUDY_LIST) }) {

                                Row {
                                    Icon(
                                        imageVector = Icons.Default.List,
                                        contentDescription = "다른 스터디 탐색"
                                    )
                                    Text(
                                        "다른 스터디 탐색",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                    )
                                }
                            }
                        }

                    }
                }
            }
        }

    }


        rememberStudyResponseList.value.forEach {
            StudyCard(
                study = it,
                navController = navController,
                onClick = { id -> navController.navigate("study/$id") },
                isPreview = false
            )
        }



}


