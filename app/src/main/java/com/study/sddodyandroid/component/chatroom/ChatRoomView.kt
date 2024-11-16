package com.study.sddodyandroid.component.chatroom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavHostController
import com.example.compose.jetchat.theme.JetchatTheme
import com.study.sddodyandroid.common.rememberChatRoomViewModel
import com.study.sddodyandroid.component.conversation.ConversationContent
import com.study.sddodyandroid.component.conversation.ConversationUiState
import com.study.sddodyandroid.component.conversation.Message
import com.study.sddodyandroid.dto.ChatInfoDto
import com.study.sddodyandroid.dto.ChatRoomDto
import com.study.sddodyandroid.service.setChatRoomInfo
import com.study.sddodyandroid.service.formatDate
import com.study.sddodyandroid.service.readChat
import com.study.sddodyandroid.service.setChatList
import java.io.File

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


@Composable
fun ChatRoomView(
    navController : NavHostController,
    studyId: Long, imageUriList: MutableState<MutableList<File>>,
    modifier: Modifier = Modifier,
    onImageUpload: () -> Unit){

    val context = LocalContext.current
    val curChatRoomInfo :MutableState<ChatRoomDto?> =remember { mutableStateOf(null) }
    val isLoadingEnd = remember {mutableStateOf(false)}
    val chatDtoList: MutableState<List<ChatInfoDto>?> = remember { mutableStateOf(null) }

    /**
     * 채팅방 기존 브로드캐스팅 개선점
     *             - 항상 setChatInfo하는데 그러지말고 메시지 정보를 받아서 클라이언트에서 추가하는 방식으로(부하가 너무 심할것으로 예상됨)
     */
    val updateUIReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // UI 업데이트 로직
            context?.let {

                setChatList(
                    it,
                    studyId = studyId,
                    chatInfoDtoList = chatDtoList,
                    isLoadingEnd = isLoadingEnd,
                )
            }
        }
    }

    val intentFilter = IntentFilter("UI_BROAD_CAST")
    LocalBroadcastManager.getInstance(LocalContext.current).registerReceiver(updateUIReceiver, intentFilter)

    val curUserNickname = LocalContext.current.getSharedPreferences("KakaoLoginPreferences",Context.MODE_PRIVATE).getString("nickname",null)
    val viewModel = rememberChatRoomViewModel()

    Column (modifier){
//        val test = remember { mutableStateOf(false) };
        if(!isLoadingEnd.value){
            setChatList(context,studyId,chatDtoList,isLoadingEnd)
        }
        setChatRoomInfo(studyId,curChatRoomInfo,viewModel)
        readChat(context,studyId)

        if(!isLoadingEnd.value){
            LoadingIndicator()
        }else{

            val messageList: MutableList<Message> = setMessageList(chatDtoList)

            JetchatTheme {
                ConversationContent(
                    chatRoomId = studyId,
                    uiState = ConversationUiState(
                        channelName= curChatRoomInfo.value?.studyName ?: "",
                        channelMembers = curChatRoomInfo.value?.studyMemberNum ?: 0,
                        initialMessages = messageList,
                        isChatRoomOver = curChatRoomInfo.value?.isChatOver ?: false
                    ),
                    navigateToProfile = { userId ->
                    },
                    uploadImageUriList = imageUriList,
                    authorMe = curUserNickname,
                    onImageUpload = onImageUpload,
                    viewModel = viewModel,
                    chatDtoList = chatDtoList,
                    navController = navController
                ) {
                    imageUri ->
                    navController.navigate("image/zoom/$imageUri")
                }
            }
        }
    }
}

@Composable
private fun setMessageList(chatDtoList: MutableState<List<ChatInfoDto>?>): MutableList<Message> {
    val messageList: MutableList<Message> = mutableListOf()
    chatDtoList.value?.forEach {
        val message = it.createdDate?.let { it1 ->
            Message(
                author = it.writeMemberNickname,
                content = it.msg,
                timestamp = formatDate(it1),
                originTime = it.createdDate,
                imageList = it.imageSrcList,
                profileImgSrc = it.writeMemberProfileSrc,
                id = it.writeMemberId
            )
        }

        if (message != null) {
            messageList.add(message)
        }

    }
    return messageList
}
