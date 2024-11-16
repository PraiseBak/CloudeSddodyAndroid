package com.study.sddodyandroid.ui.component

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.DrawerState
import androidx.compose.material.ModalDrawer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.common.RetrofitInstance
import com.study.sddodyandroid.common.StudyAdminViewModel
import com.study.sddodyandroid.component.conversation.AsyncImageWithHeader
import com.study.sddodyandroid.component.main.profile.Toolbar
import com.study.sddodyandroid.dto.AlarmDto
import com.study.sddodyandroid.dto.MemberInfoDto
import com.study.sddodyandroid.dto.StudyIdMemberIdDto
import com.study.sddodyandroid.service.studyMemberDelete
import com.study.sddodyandroid.ui.common.CommonBackgroundColor
import com.study.sddodyandroid.ui.common.CommonButtonColor
import com.study.sddodyandroid.ui.common.CommonCardBackgroundColor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ManagePeopleDrawer(
    drawerState: DrawerState,
    studyAdminViewModel: StudyAdminViewModel,
    navController: NavHostController,
) {
    val context = LocalContext.current
    val sharedPreferences = LocalContext.current.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE)
    val nickname = sharedPreferences.getString("nickname","")

    // Drawer가 열려있는지 여부에 따라서 Drawer를 보여줄지 결정
    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Drawer에 표시될 콘텐츠
            val studyDto = studyAdminViewModel.getStudyDto()
            val isAdmin = if(studyDto?.ownerMemberInfo?.nickname == nickname && nickname != null) true else false

            Toolbar(title = "스터디 인원관리")

            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)){
                Icon(Icons.Filled.People, contentDescription = "사람 수")
                studyDto?.let {studyDto ->
                    Text(text = "${studyDto.studyMemberInfoDtoList.size} / ${studyDto.maxStudyMemberNum}")
                }
            }
            HorizontalDivider()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            ) {

                studyDto?.studyMemberInfoDtoList?.forEach {
                    Row {
                        Box(modifier = Modifier.weight(1.5f)) {
                            AsyncImageWithHeader(
                                uri = it.memberProfileImgSrc,
                                isProfile = true,
                                onProfileClick = {
                                    navController.navigate("PROFILE/${it.id}")
                                }
                            )
                        }

                        Box(modifier = Modifier
                            .weight(4f)
                            .padding(start = 8.dp)) {

                            Column {
                                Text(text = it.nickname)

                                if (it.nickname == studyDto.ownerMemberInfo.nickname) {
                                    Button(onClick = {}, enabled = false) {
                                        Text(text = "관리자")
                                    }
                                } else {
                                    Button(onClick = {}, enabled = false) {
                                        Text(text = "멤버")
                                    }
                                }
                            }
                        }

                        //관리자가 아니며 자기자신이 아닌경우 삭제 가능
                        Box(modifier = Modifier
                            .weight(2f)
                            .padding(top = 18.dp)) {
                            if(isAdmin && (it.nickname != nickname)){

                                Button(onClick = { studyMemberDelete(StudyIdMemberIdDto(studyId = studyDto.studyId,memberId = it.id),
                                    navController = navController,context = context )}
                                    ,colors = CommonButtonColor()) {
                                    Text(text = "추방")
                                }
                            }
                        }
                    }
                    HorizontalDivider()
                }
            }
        },
        content = {
            // 메인 콘텐츠
//            Button(onClick = { drawerState.open() }) {
//                Text("Open Drawer")657q
//            }
        }
    )
}


@Composable
fun ShowMemberDrawer(
    drawerState: DrawerState,
    memberList: List<MemberInfoDto>?,
) {
    val context = LocalContext.current
    val sharedPreferences = LocalContext.current.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE)

    // Drawer가 열려있는지 여부에 따라서 Drawer를 보여줄지 결정
    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            Toolbar(title = "채팅 인원")

            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)){
                Icon(Icons.Filled.People, contentDescription = "사람 수")

                Text(text = "${memberList?.size ?: 0}")
            }
            HorizontalDivider()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            ) {

                memberList?.forEach {
                    Row {
                        Box(modifier = Modifier.weight(1.5f)) {
                            AsyncImageWithHeader(
                                uri = it.memberProfileImgSrc,
                                isProfile = true
                            )
                        }

                        Box(modifier = Modifier
                            .weight(4f)
                            .padding(start = 8.dp)) {

                            Column {
                                Text(text = it.nickname)
                            }
                        }
                    }
                    HorizontalDivider()
                }
            }
        },
        content = {
            // 메인 콘텐츠
//            Button(onClick = { drawerState.open() }) {
//                Text("Open Drawer")657q
//            }
        }
    )
}

@Composable
fun readAlarm(){
    val apiService = RetrofitInstance.apiService

    val call = apiService.readAlarm()
    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
        }
    })
}

@Composable
fun AlarmDrawer(
    drawerState: DrawerState,
    navController: NavHostController,
    rememberAlarmDto: MutableList<AlarmDto>
) {
    val context = LocalContext.current
    val sharedPreferences = LocalContext.current.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE)

    if(drawerState.isOpen){
        readAlarm()
    }

    // Drawer가 열려있는지 여부에 따라서 Drawer를 보여줄지 결정
    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            Toolbar(title = "알람")
            HorizontalDivider()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
            ) {
                item{
                    rememberAlarmDto.forEach{
                        Card(modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .background(if (it.isRead) Color.Companion.LightGray.copy(0.5f) else CommonCardBackgroundColor())
                        ) {
                            Row(modifier = Modifier
                                .clickable {
                                    navController.navigate("${it.alarmCategory.info}/${it.moveUriId}")
                                }
                            ){
                                Column(modifier = Modifier.padding(8.dp)) {
                                    val splitArr = it.info.split("\n")
                                    if(splitArr.size > 1){
                                        val studyTitle = splitArr[0]
                                        val content = splitArr[1]

                                        Text(text = studyTitle,fontSize = 18.sp,
                                            fontWeight = FontWeight.Medium)
                                        Text(text = content)
                                    }else{
                                        Text(text = it.info)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        content = {
            // 메인 콘텐츠
//            Button(onClick = { drawerState.open() }) {
//                Text("Open Drawer")657q
//            }
        }

    )



}