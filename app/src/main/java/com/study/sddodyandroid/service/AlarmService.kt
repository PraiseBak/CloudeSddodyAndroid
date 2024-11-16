package com.study.sddodyandroid.service

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.messaging.messaging
import com.study.sddodyandroid.common.RetrofitInstance
import com.study.sddodyandroid.dto.AlarmDto
import com.study.sddodyandroid.service.ChatService.Companion.setSubscribeInfoList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun initNotificationSubscribe(topic : String) {

    Firebase.messaging.subscribeToTopic(topic)
        .addOnCompleteListener { task ->
            println("구독완 $topic")
        }
        .addOnFailureListener { exception ->
            println("실패")
            exception.printStackTrace()
        }
}


fun setAlarmDto(context: Context, alarmDtoList: MutableState<MutableList<AlarmDto>?>) {
    val apiService = RetrofitInstance.apiService
    val call = apiService.getAlarmDto()

    call.enqueue(object : Callback<List<AlarmDto>> {
        override fun onResponse(
            call: Call<List<AlarmDto>>,
            response: Response<List<AlarmDto>>
        ) {
            response.body()?.let {
                alarmDtoList.value = it.toMutableList()
            } ?: {
                alarmDtoList.value = mutableListOf()
            }

        }

        override fun onFailure(call: Call<List<AlarmDto>>, t: Throwable) {
            Toast.makeText(context, "알람 정보를 가져오는데 실패하였습니다", Toast.LENGTH_SHORT).show()
        }
    })
}

