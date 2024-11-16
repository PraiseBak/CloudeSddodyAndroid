package com.study.sddodyandroid.component.main

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CancelPresentation
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.GroupRemove
import androidx.compose.material.icons.filled.RememberMe
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.activity.MainViewRoute
import com.study.sddodyandroid.activity.StartActivity
import com.study.sddodyandroid.common.setGithubMemberInfo
import com.study.sddodyandroid.component.start.kakaoLogout
import com.study.sddodyandroid.dto.GithubUserProfileDto
import com.study.sddodyandroid.dto.MemberInfoDto
import com.study.sddodyandroid.helper.member.MemberStatus
import com.study.sddodyandroid.service.changeMemberInfo
import com.study.sddodyandroid.service.getMemberInfo
import com.study.sddodyandroid.ui.common.CommonSecondaryBackgroundColor
import com.study.sddodyandroid.ui.component.study.PopupMessage


enum class MemberSettingEnum(val info: String, val url: String, val icon: ImageVector){
    CHANGE_MEMBER_INFO("유저 정보 변경","user/modifyUser",Icons.Default.RememberMe),
    CHANGE_MEMBER_STATUS_SLEEP("회원 탈퇴","${MainViewRoute.PROFILE}/0",Icons.Default.GroupRemove),
    CHANGE_MEMBER_STATUS_NON_SLEEP("유저 활성화","${MainViewRoute.PROFILE}/0",Icons.Default.GroupAdd),
    CHANGE_MEMBER_GITHUB_CONNECT("깃허브 연동 해지","${MainViewRoute.PROFILE}/0",Icons.Default.CancelPresentation),
    LOGOUT("로그아웃","",Icons.AutoMirrored.Filled.Logout)
}

@Composable
fun MemberSetting(navController : NavHostController){
    val context = LocalContext.current
    val rememberMemberInfo: MutableState<MemberInfoDto?> = remember { mutableStateOf(null) }
    val rememberIsLoading = remember{ mutableStateOf(false) }
    val rememberShowDialog = remember{ mutableStateOf(false) }
    var rememberIsGithubConnected = remember{ mutableStateOf(false) }

    getMemberInfo(context = context,rememberIsLoading = rememberIsLoading, memberId = 0L, rememberMemberInfo = rememberMemberInfo)

    var isSleepMember = remember{ mutableStateOf(false)}

    //비활성화된 유저인가
    rememberMemberInfo.value?.let {
        isSleepMember.value = it.memberStatus == MemberStatus.ROLE_DELETED
        rememberIsGithubConnected.value = it.githubNickname != null
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "유저 설정 변경", fontSize = 28.sp)

        var userChangeStatusText = "탈퇴시 작성한 게시물과 스터디가 종료됩니다.\n" +
                "이후 다시 복귀할 수 있으나\n" +
                "삭제된 게시물은 복귀되지 않습니다."
        var userChangeStatusTitle = "유저 탈퇴하기"

        MemberSettingEnum.entries.forEach{
            if(isSleepMember.value && it == MemberSettingEnum.CHANGE_MEMBER_STATUS_SLEEP){
            }
            else if(!isSleepMember.value && it == MemberSettingEnum.CHANGE_MEMBER_STATUS_NON_SLEEP){
            //
            }else if(!rememberIsGithubConnected.value && it == MemberSettingEnum.CHANGE_MEMBER_GITHUB_CONNECT){

            } else{
                //활성화
                if(it == MemberSettingEnum.CHANGE_MEMBER_STATUS_NON_SLEEP){
                    userChangeStatusText = "복귀하시겠습니까?\n" +
                            "삭제된 게시물은 복귀되지 않습니다"
                    userChangeStatusTitle = "유저 복귀"
                }

                Card(colors = CardDefaults.cardColors(
                    containerColor = CommonSecondaryBackgroundColor(),
                ), modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp)
                    .padding(8.dp)
                    .clickable {
                        when (it) {
                            MemberSettingEnum.CHANGE_MEMBER_INFO -> {
                                val intent = Intent(context, StartActivity::class.java).apply {
                                    putExtra("isModifyUser", true)
                                }
                                context.startActivity(intent)
                            }

                            MemberSettingEnum.LOGOUT -> {
                                val sharedPreferences = context.getSharedPreferences("KakaoLoginPreferences",Context.MODE_PRIVATE)
                                kakaoLogout(context,sharedPreferences)

                                val intent = Intent(context, StartActivity::class.java).apply {}
                                context.startActivity(intent)
                            }

                            MemberSettingEnum.CHANGE_MEMBER_GITHUB_CONNECT -> {
                                setGithubMemberInfo(GithubUserProfileDto(null, -1, -1), context)
                            }

                            else -> {
                                rememberShowDialog.value = true
                            }
                        }
                    }){


                    Row(modifier = Modifier.padding(8.dp)){
                        Icon(it.icon,contentDescription = null)
                        Text(modifier = Modifier.padding(horizontal = 20.dp),text = it.info)
                    }

                }
            }
        }

        PopupMessage(
            showDialog = rememberShowDialog,
            askStr = userChangeStatusTitle,
            contentText = userChangeStatusText,
            onClick = {
                changeMemberInfo(context, navController, onEnd = {
                    val intent = Intent(context, StartActivity::class.java)
                    val sharedPreferences = context.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE)
                    sharedPreferences.edit().clear().apply()
                    context.startActivity(intent)
                })
            }
        )


    }
}