package com.study.sddodyandroid.component.study

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.activity.MainViewRoute
import com.study.sddodyandroid.common.StudyAdminViewModel
import com.study.sddodyandroid.component.chatroom.LoadingIndicator
import com.study.sddodyandroid.component.community.CommunityContentCardList
import com.study.sddodyandroid.component.conversation.AsyncImageWithHeader
import com.study.sddodyandroid.component.main.profile.GithubUserCard
import com.study.sddodyandroid.component.start.GoogleMapViewBySpecificLocation
import com.study.sddodyandroid.dto.BoardResponseDto
import com.study.sddodyandroid.dto.MemberInfoDto
import com.study.sddodyandroid.dto.StudyResponseDto
import com.study.sddodyandroid.service.getMemberBoardList
import com.study.sddodyandroid.service.getRequestMemberList
import com.study.sddodyandroid.service.getStudy
import com.study.sddodyandroid.ui.common.CommonButtonColor
import com.study.sddodyandroid.ui.common.CommonButtonOnlyColor
import com.study.sddodyandroid.ui.component.study.SddodyTopAppBar
import com.study.sddodyandroid.ui.component.study.StudyAdminViewBottomBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StudyAdminView(
    navController: NavHostController,
    studyId: Long,
    studyAdminViewModel: StudyAdminViewModel,
       ) {

    val context = LocalContext.current
    val rememberRequestMemberList : MutableState<List<MemberInfoDto>?> = remember{ mutableStateOf(null) }
    val rememberMemberPortfolioList : MutableState<MutableList<BoardResponseDto>> = remember{ mutableStateOf(mutableListOf()) }
    val rememberIsLoading = remember{ mutableStateOf(false) }
    val rememberStudyLoading = remember{ mutableStateOf(false) }
    var curIndex = remember { mutableIntStateOf(0) }
    var rememberStudy : MutableState<StudyResponseDto?> = remember{mutableStateOf(null)}

    getRequestMemberList(context,studyId,rememberRequestMemberList)
    getStudy(id=studyId,context,rememberStudy,rememberStudyLoading,studyAdminViewModel)


    Scaffold(topBar = {
        SddodyTopAppBar("스터디 관리 페이지", true, Modifier, navController, studyAdminViewModel,rememberStudy = rememberStudy,isAdmin = true) },
        bottomBar = {
            val requestMember = rememberRequestMemberList.value?.getOrNull(curIndex.intValue)
            requestMember?.id?.let {
                StudyAdminViewBottomBar(
                    studyId = studyId, requestMemberId = it,
                    rememberRequestMemberList = rememberRequestMemberList,
                    curIndex = curIndex,
                    navController = navController
                )
            }
        },
    ){
        LazyColumn(modifier = Modifier
            .padding(it)
            .fillMaxSize()
            .background(color = Color.White.copy(0.8f))
        ){

            rememberRequestMemberList.value?.let { requestMemberList ->
                if(requestMemberList.isNotEmpty()){
                    requestMemberList[curIndex.value]?.let {
                        getMemberBoardList(
                            it.id,
                            context,
                            rememberMemberPortfolioList,
                            rememberIsLoading
                        )

                        item {
                            ParticipationProfileView(it,navController)

                            Text("포트폴리오", style = MaterialTheme.typography.headlineSmall)

                            if (!rememberIsLoading.value) {
                                LoadingIndicator()
                            } else {
                                if (rememberMemberPortfolioList.value.size == 0) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "아직 작성한 포트폴리오가 없어요",
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                CommunityContentCardList(
                                    navController = navController,
                                    rememberCommunityContent = rememberMemberPortfolioList.value,
                                    noCommentInput = true
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }else{
                    item {
                        Column(

                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "아직 참여 신청한 사람이 없어요",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun InteractionArea(
    rememberRequestMemberList: MutableState<List<MemberInfoDto>?>,
    curIndex: MutableIntState,
    deniedOnClick: () -> Unit,
    acceptOnClick: () -> Unit
) {
    val context = LocalContext.current
    rememberRequestMemberList.value?.let{

        Row(
            modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
        ){
            IconButton(onClick = {
                if(curIndex.value -1 <= -1){
                    Toast.makeText(context, "첫번째 포트폴리오입니다", Toast.LENGTH_SHORT).show()
                }else{
                    curIndex.value -= 1
                }
            }) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = "이전 사람 보기")
            }

            Button(onClick = { acceptOnClick() },
                colors = CommonButtonColor()
            ) {
                Text(text = "함께하기")
            }

            Button(onClick = { deniedOnClick() },
                colors = CommonButtonColor()
            ) {
                Text(text = "다음 기회에")
            }

            IconButton(
                onClick = {
                    if(curIndex.value +1 >= it.size){
                        Toast.makeText(context, "마지막 포트폴리오입니다", Toast.LENGTH_SHORT).show()
                    }else{
                        curIndex.value +=1
                    }
                }) {
                Icon(Icons.Filled.ChevronRight, contentDescription = "다음 사람 보기")
            }
        }
    }


//    Column(
//        verticalArrangement = Arrangement.Center,
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
////            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(text = "ㅇㅇ")
//            rememberRequestMemberList.value?.let{
//
//                IconButton(onClick = {
//                    if(curIndex.value -1 <= -1){
//                        Toast.makeText(context, "첫번째 포트폴리오입니다", Toast.LENGTH_SHORT).show()
//                    }else{
//                        curIndex.value -= 1
//                    }
//                }) {
//                    Icon(Icons.Outlined.ChevronLeft, contentDescription = "이전 사람 보기")
//                }
//
//                IconButton(onClick = {
//                    if(curIndex.value +1 >= it.size){
//                        Toast.makeText(context, "마지막 포트폴리오입니다", Toast.LENGTH_SHORT).show()
//                    }else{
//                        curIndex.value +=1
//                    }
//                }) {
//                    Icon(Icons.Outlined.ChevronRight, contentDescription = "다음 사람 보기")
//                }
//            }
//        }
//    }
}


@Composable
fun ParticipationProfileView(memberInfoDto: MemberInfoDto, navController: NavHostController) {
        //이미지 및 닉네임
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("${MainViewRoute.PROFILE}/${memberInfoDto.id}}")
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            AsyncImageWithHeader(
                uri = memberInfoDto.memberProfileImgSrc,
                isProfile = true,
                onProfileClick = {
                    navController.navigate("PROFILE/${memberInfoDto.id}")
                }
            )
            Text(memberInfoDto.nickname)
        }

        HorizontalDivider()

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(
                    color = CommonButtonOnlyColor(),
                    RoundedCornerShape(8.dp)
                )
                .padding(20.dp)
                .fillMaxWidth()
        ){
            Text(memberInfoDto.selfIntroduce)
        }

        //오른쪽 정렬된 year level 등 추가정보
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.End){
            DevInfoArea(memberInfoDto = memberInfoDto)
        }

        Text("관심있는 분야",
            style = MaterialTheme.typography.headlineSmall,
        )

        LazyRow(){
            items(memberInfoDto.developerEnumList){
                Button(colors = CommonButtonColor(),
                    onClick={},
                    modifier = Modifier.padding(start = 4.dp)) {
                    Text(text = it.info)
                }
            }
        }

        Text("관심있는 언어",
            style = MaterialTheme.typography.headlineSmall,
        )

        LazyRow(){
            items(memberInfoDto.devLanguageEnumList){
                Button(colors = CommonButtonColor(),
                    onClick={},
                    modifier = Modifier.padding(start = 4.dp)) {
                    Text(text = it.info)
                }
            }
        }

        if(memberInfoDto.githubNickname != null) GithubUserCard(memberInfoDto)

        Text("활동 지역",
            style = MaterialTheme.typography.headlineSmall,
        )

        Column {
            GoogleMapViewBySpecificLocation(location = memberInfoDto.location)
        }
}

@Composable
fun DevInfoArea(memberInfoDto: MemberInfoDto){
    Row() {
        Icon(
            imageVector = Icons.Default.Work,
            contentDescription = "직종",
            modifier = Modifier
                .size(28.dp)
                .padding(start = 10.dp)
        )

        Text(
            text = memberInfoDto.devLevelEnum.text,
            modifier = Modifier
                .padding(start = 4.dp, top = 6.dp)
        )
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = "개발 공부 기간",
            modifier = Modifier
                .size(28.dp)
                .padding(start = 10.dp)
        )

        Text(
            text = memberInfoDto.devYearEnum.yearStr,
            modifier = Modifier
                .padding(start = 4.dp, top = 6.dp)
        )

    }
}