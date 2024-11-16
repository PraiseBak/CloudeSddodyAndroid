package com.study.sddodyandroid.ui.component.study

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.dto.StudyResponseDto
import com.study.sddodyandroid.helper.study.StudyStateEnum
import com.study.sddodyandroid.service.studyParticipationRequest
import com.study.sddodyandroid.ui.common.CommonButtonColor

@Composable
fun PopupParticipationMessage(
    showDialog: MutableState<Boolean>, id: Long, context: Context,

    ) {
    var text by remember { mutableStateOf("") }
    val MAX_LENGTH = 400

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (showDialog.value) {
            AlertDialog(

                onDismissRequest = { showDialog.value = false },
                title = { Text("참여 신청에 전달할 한마디") }, // 타이틀 변경
                text = {
                    TextField(
                        value = text,
                        onValueChange = {
                            if (it.length <= MAX_LENGTH) { // 200자 제한
                                text = it
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                        ,

                        supportingText = {
                            Text(
                                text = "${text.length} / $MAX_LENGTH",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                            )
                        }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog.value = false
                            studyParticipationRequest(studyId = id, context = context, message = text)
                        }
                    ) {
                        Text("제출") // 버튼 텍스트 변경
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog.value = false }
                    ) {
                        Text("취소") // 버튼 텍스트 변경
                    }
                },
            )
        }
    }
}

@Composable
fun StudyDetailBottomBar(
    modifier: Modifier,
    study: StudyResponseDto,
    navController: NavHostController,
    context: Context,
    studyId: Long,
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE)
    val myNickname = sharedPreferences.getString("nickname","")
    var isStudyParticipation = false
    var isStudyOwner = study.ownerMemberInfo.nickname == myNickname
    //참여한 스터디인지
    study.studyMemberInfoDtoList.forEach{
        if(it.nickname == myNickname){
            isStudyParticipation = true
            return@forEach
        }
    }

    Surface(
        shadowElevation = 7.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)

        ) {
            if(isStudyParticipation){
                Button(onClick = {navController.navigate("chatRoom/${studyId}")},
                    colors = CommonButtonColor()
                ) {
                    Text(text = "채팅 참여하기")
                }
            }else{
                if(study.studyMemberInfoDtoList.size == study.maxStudyMemberNum){
                    Button(onClick = {},
                        colors = CommonButtonColor(),
                        enabled = false,
                        ) {
                        Text(text = "이미 스터디가 꽉찼어요!")
                    }
                }else{
                    var showDialog = remember { mutableStateOf(false) }
                    if(showDialog.value){
                        if(study.isOpenToEveryone){
                            studyParticipationRequest(studyId = studyId, context = context, message = "")
                        }else{
                            PopupParticipationMessage(showDialog,studyId,context)
                        }

                    }

                    Button(onClick = {showDialog.value = true},
                        colors = CommonButtonColor()
                    ){
                        Text(text = "스터디 참여 신청하기")
                    }
                }
            }

            if(isStudyOwner && study.studyStateEnum != StudyStateEnum.END){
                Button(onClick = {
                                 navController.navigate("study/${studyId}/admin")
                },
                    colors = CommonButtonColor()
                    ) {
                    Text("스터디 관리하기")
                }
            }


            if(study.studyStateEnum == StudyStateEnum.END){
                Button(onClick = {
                    navController.navigate("community/add/${studyId}")
                },
                    colors = CommonButtonColor()
                ) {
                    Text("스터디 후기쓰기")
                }
            }

        }
    }
}