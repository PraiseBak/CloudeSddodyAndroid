package com.study.sddodyandroid.service

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.TextFieldValue
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.messaging
import com.study.sddodyandroid.common.RetrofitInstance
import com.study.sddodyandroid.dto.ChatDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ChatService{
    companion object {
        private const val TAG = "FirebaseChatService"

         fun setSubscribeInfoList(
        ){
            val apiService = RetrofitInstance.apiService
            val call = apiService.getSubscribeInfoList()
            call.enqueue(object : Callback<List<String>> {
                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {
                    response.body()?.forEach{
                        FirebaseMessaging.getInstance().subscribeToTopic(it)
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                }

            })
        }

        fun sendMsg(
            chatDto: ChatDto?,
            chatRoomId: Long,
            context: Context,
            sendImageList: MutableState<MutableList<File>>,
            textState: MutableState<TextFieldValue>,
        ){
            //image URL 변환해줌
            val imageList: List<MultipartBody.Part> = sendImageList.value.mapNotNull { imageUrl ->
                imageUrl?.let {
                    val file = it // 이미지 파일 경로로부터 File 객체 생성
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val imagePart = MultipartBody.Part.createFormData("imageList[]", file.name, requestFile)

                    imagePart
                }
            }

            val apiService = RetrofitInstance.apiService
            val call = apiService.sendChat(chatRoomId,chatDto,imageList)

            call?.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    textState.value = TextFieldValue()
                    sendImageList.value = mutableListOf()
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(context, "메시지를 보내는데 실패하였습니다", Toast.LENGTH_SHORT).show()
                }
            })


        }

        fun initMemberSubscribe(topic : String) {
            Firebase.messaging.subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    setSubscribeInfoList()
                }
        }

    }
}

// imageBody = MultipartBody.Part.createFormData("profileImage", profileImage?.name, requestFile)

