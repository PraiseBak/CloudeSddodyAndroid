package com.study.sddodyandroid.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.study.sddodyandroid.dto.ChatRoomDto


class ChatRoomViewModel : ViewModel() {
    val chatRoomDto : MutableState<ChatRoomDto?> = mutableStateOf(null)

    fun updateChatRoomDto(chatRoomDto: ChatRoomDto){
        this.chatRoomDto.value = chatRoomDto
    }
}

@Composable
fun rememberChatRoomViewModel() : ChatRoomViewModel{
    return remember {
        ChatRoomViewModel()
    }
}