package com.study.sddodyandroid.ui.component.study

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.component.study.InteractionArea
import com.study.sddodyandroid.dto.MemberInfoDto
import com.study.sddodyandroid.service.adminStudyCheckParticipation

@Composable
fun PopupMessage(
    showDialog: MutableState<Boolean>,
    askStr: String = "",
    onClick: () -> Unit,
    contentText:  String = "",
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = { Text(askStr) }, // 타이틀 변경
                text = {Text(contentText )},
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog.value = false
                            //TODO 디버깅
//                            adminStudyCheckParticipation(isAccept = isAccept,
//                                context = context,
//                                studyId = studyId,
//                                navController = navController,
//                                requestMemberId = requestMemberId
//                                )
                            onClick()
                        }
                    ) {
                        Text("확인") // 버튼 텍스트 변경
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
fun StudyAdminViewBottomBar(
    navController: NavHostController,
    studyId: Long,
    requestMemberId: Long,
    curIndex: MutableIntState,
    rememberRequestMemberList: MutableState<List<MemberInfoDto>?>
) {
    val context = LocalContext.current
    var rememberShowDialog = remember {mutableStateOf(false)}
    var rememberIsAccept by remember {mutableStateOf(false)}

    if(rememberShowDialog.value){
        PopupMessage(
            showDialog = rememberShowDialog,
            askStr = if(rememberIsAccept)"유저의 스터디 참가신청을 허용하시겠습니까?" else "해당 유저의 참가신청을 거절하시겠습니까?",
            onClick = {
                adminStudyCheckParticipation(
                    isAccept = rememberIsAccept,
                    context = context,
                    studyId = studyId,
                    navController = navController,
                    requestMemberId = requestMemberId
                )
            }
        )



    }

    Surface(
        shadowElevation = 7.dp,
        modifier = Modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp) // 아이콘과 텍스트 간격 조절
        ) {
            //상호작용 버튼 부분
            InteractionArea(rememberRequestMemberList = rememberRequestMemberList, curIndex = curIndex,
                acceptOnClick = {
                    rememberIsAccept = true
                    rememberShowDialog.value = true
                },
                deniedOnClick = {
                    rememberIsAccept = false
                    rememberShowDialog.value = true
                }
            )




        }
    }
}