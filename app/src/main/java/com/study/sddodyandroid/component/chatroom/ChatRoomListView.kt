
package com.study.sddodyandroid.component.chatroom

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.activity.MainViewRoute
import com.study.sddodyandroid.common.RetrofitInstance
import com.study.sddodyandroid.component.conversation.AsyncImageWithHeader
import com.study.sddodyandroid.dto.ChatRoomDto
import com.study.sddodyandroid.ui.common.CommonBackgroundColor
import com.study.sddodyandroid.ui.common.CommonButtonColor
import com.study.sddodyandroid.ui.common.CommonCardBackgroundColor
import com.study.sddodyandroid.ui.common.CommonSecondaryBackgroundColor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun ChatRoomListView(navController: NavHostController) {
    val context = LocalContext.current
    var chatRoomList: MutableList<ChatRoomDto> = mutableListOf()
    var rememberChatRoomList = remember { mutableStateOf(chatRoomList) }

    if(rememberChatRoomList.value.size == 0){
        getChatRoomList(rememberChatRoomList)
    }else{
        FilledChatRoomList(rememberChatRoomList,
            onClick={studyId -> navController.navigate("chatRoom/$studyId")},)
    }

}


@Composable
fun FilledChatRoomList(rememberChatRoomList: MutableState<MutableList<ChatRoomDto>>,onClick : (Long) -> Unit ) {

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
    ) {
        items(rememberChatRoomList.value) {
            ChatRoomCard(onClick, it)
        }
    }
}

@Composable
fun ChatRoomCard(
    onClick: (Long) -> Unit,
    it: ChatRoomDto,
) {
    val sharedPreferences = LocalContext.current.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE)
    val memberId = sharedPreferences.getString("memberId","0")

    Card(
        colors = CardDefaults.cardColors(
            containerColor = CommonSecondaryBackgroundColor(),
        ),
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .clickable { onClick(it.studyId) }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // 제목
            Text(
                text = it.studyName,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            )

            var msg = it.recentMsg ?: "아직 메시지가 없어요"
            if(msg.isEmpty()) msg = "아직 메시지가 없어요"

            val readableCount = it.chatReadMap?.getOrDefault(memberId?.toLong(),0)
            if(readableCount != 0L && readableCount != null){
                Text(text = readableCount.toString())
            }

            Text(text = msg,
                style = MaterialTheme.typography.bodyMedium)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp) // 아이콘과 텍스트 간격 조절
            ) {
                Icon(Icons.Outlined.People, contentDescription = "Localized description")
                Text(
                    text = it.studyMemberNum.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(0.dp) // 아이콘과 텍스트 간격 조절
            ) {
                it.chatRoomMemberInfoDtoList.forEach{
                    AsyncImageWithHeader(uri = it.memberProfileImgSrc,isProfile = true)
                }
            }
            if(it.isChatOver){
                Text(text = "종료된 채팅방입니다")
            }
        }
    }
}

fun getChatRoomList(rememberChatRoomList: MutableState<MutableList<ChatRoomDto>>) {
    val apiService = RetrofitInstance.apiService

    apiService.getChatRoomList().enqueue(object : Callback<List<ChatRoomDto>> {
        override fun onResponse(call: Call<List<ChatRoomDto>>, response: Response<List<ChatRoomDto>>) {
            if (response.isSuccessful) {
                var chatRoomList: List<ChatRoomDto>? = response.body()
                // 여기에서 chatRoomList를 사용하거나 처리
                chatRoomList?.let {
                    rememberChatRoomList.value = it.toMutableList()
                }
            } else {
                // API 호출은 성공했지만 응답이 실패한 경우의 처리
            }
        }

        override fun onFailure(call: Call<List<ChatRoomDto>>, t: Throwable) {
            // API 호출 자체가 실패한 경우의 처리
        }
    })
}
