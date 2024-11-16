package com.study.sddodyandroid.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.study.sddodyandroid.activity.MainActivity
import com.study.sddodyandroid.common.ChatRoomViewModel
import com.study.sddodyandroid.common.RetrofitInstance
import com.study.sddodyandroid.dto.ChatInfoDto
import com.study.sddodyandroid.dto.ChatRoomDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class FCMService : FirebaseMessagingService(){

    //mebmerId를 id로 사용한다
    //현재는 study로만 이동 study/{moveURLId}
    private fun sendNotification(title: String, messageBody: String,moveURLId : String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("moveStudyUrl",moveURLId)
        }

        val flags = PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getActivity(this, 0,intent, flags )
        val notificationId = System.currentTimeMillis().toInt()

        // 알림을 생성하고 설정
        val notificationBuilder = NotificationCompat.Builder(this, notificationId.toString())
            .setSmallIcon(com.study.sddodyandroid.R.drawable.book2) // 알림 아이콘 설정
            .setContentTitle(title) // 알림 제목 설정
            .setContentText(messageBody) // 알림 내용 설정
            .setAutoCancel(true) // 사용자가 알림을 탭하면 자동으로 알림을 제거
            .setContentIntent(pendingIntent) // 사용자가 알림을 탭하면 실행될 Intent 설정

        // Android Oreo(26 API 레벨) 이상에서는 알림 채널을 설정해야 함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            println("oo")
            val channelId = notificationId.toString()
            val channelName = "sddody_channel"
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }else{
            println("xx")
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // 수신한 FCM 메시지 처리
        Log.d(TAG, "From: ${remoteMessage.from}")

        //true면 메인화면에서 알람
        //false면 채팅룸 알람
        val isNotificationAlarm = remoteMessage.from?.run {
            when {
                contains("notification") -> true
                else -> false
            }
        } ?: false


        /**
         * 메시지와 데이터 구분해야함
         * 포그라운드 - 데이터에서 받은걸로
         * 백그라운드 - 푸쉬알람은 항상 보내는 걸로, 데이터는 백그라운드인경우 가지 않음
         */

        // 메시지 데이터 페이로드 확인
        remoteMessage.data.isNotEmpty().let {
            if(it){
                Log.d(TAG, "Message data payload: " + remoteMessage.data)

                val title = remoteMessage.data["title"].run{
                    this
                } ?: ""
                val content = remoteMessage.data["content"].run{
                    this
                } ?: ""

                val id =remoteMessage.data["id"].run{
                    this
                } ?: ""

                val isChatRoomUIUpdate = id.isEmpty()
                println("isChatRoomUIUpdate $isChatRoomUIUpdate")
                println("$title,$content,$id")
                if(title != "채팅방 구독"){
                    println("채팅방 구독아님")
                    sendNotification(title,content,id)
                }

                sendBroadcastToUI(title,id,isChatRoomUIUpdate)
            }
        }
    }

    private fun sendBroadcastToUI(title: String, id: String, isChatRoomUIUpdate: Boolean) {
        val mainUIId = "MAIN_UI_BROAD_CAST"
        val chatRoomUIId = "UI_BROAD_CAST"
        val intent = Intent(if(isChatRoomUIUpdate) chatRoomUIId else mainUIId).apply {
            putExtra("TITLE", title)
            putExtra("ID", id)
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }


    override fun onNewToken(token: String) {
        // FCM 토큰이 변경될 때 호출되는 메서드
        Log.d(TAG, "Refreshed token: $token")
        // 토큰을 서버로 업데이트하거나 필요한 작업을 수행합니다.
    }

    companion object {
        private const val TAG = "FirebaseChatService"
    }
}

fun formatDate(date : Date): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
}


fun isShowTimeStamp(prevDate : Date, itDate : Date) : Boolean{
    return itDate?.let { itDate ->
        prevDate?.let { prevDate ->
            val itCalendar = Calendar.getInstance().apply {
                time = itDate
            }

            val prevCalendar = Calendar.getInstance().apply {
                time = prevDate
            }

            return itCalendar.get(Calendar.HOUR_OF_DAY) != prevCalendar.get(Calendar.HOUR_OF_DAY) ||
                    itCalendar.get(Calendar.MINUTE) != prevCalendar.get(Calendar.MINUTE)
        }
    } == true
}

fun compareAndFormatDates(itDate: Date?, prevDate: Date?): String {
    return itDate?.let { itDate ->
        prevDate?.let { prevDate ->
            val itCalendar = Calendar.getInstance().apply {
                time = itDate
            }

            val prevCalendar = Calendar.getInstance().apply {
                time = prevDate
            }

            if (itCalendar.get(Calendar.YEAR) != prevCalendar.get(Calendar.YEAR)) {
                // Different years, display year, month, and day
                SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).format(itDate)
            } else if (itCalendar.get(Calendar.MONTH) != prevCalendar.get(Calendar.MONTH)
                || itCalendar.get(Calendar.DAY_OF_MONTH) != prevCalendar.get(Calendar.DAY_OF_MONTH)
            ) {
                // Same year but different month or day, display month and day
                SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(itDate)
            } else {
                // Same year, same month, same day, no need to display year, month, and day
                ""
            }
        } ?: SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(itDate)
    } ?: ""
}


@Composable
fun readChat(context : Context,studyId : Long){
    val apiService = RetrofitInstance.apiService
    val call = apiService.readChat(studyId)

    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if(response.isSuccessful){
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
        }
    })
}

@Composable
fun setChatRoomInfo(
    chatRoomId: Long,
    curChatRoomInfo: MutableState<ChatRoomDto?>,
    viewModel: ChatRoomViewModel
) {
    if(chatRoomId.toInt() == -1) return
    val context = LocalContext.current
    val apiService = RetrofitInstance.apiService
    val call = apiService.getChatRoomInfo(chatRoomId)

    call.enqueue(object : Callback<ChatRoomDto>
    {
        override fun onResponse(call: Call<ChatRoomDto>, response: Response<ChatRoomDto>) {

            val chatRoomDto = response.body()
            val map : MutableMap<String,String> = mutableMapOf()
            chatRoomDto?.chatRoomMemberInfoDtoList?.forEach{ it->
                map[it.nickname] = it.memberProfileImgSrc
            }

            chatRoomDto?.let{
                it.chatRoomUsernameProfileSrcMap = map
                viewModel.updateChatRoomDto(it)
            }

            curChatRoomInfo.value = chatRoomDto
        }

        override fun onFailure(call: Call<ChatRoomDto>, t: Throwable) {
            Toast.makeText(context, "채팅방정보를 가져오는데 실패하였습니다", Toast.LENGTH_SHORT).show()
        }
    })

}


fun setChatList(
    context: Context,
    studyId: Long,
    chatInfoDtoList: MutableState<List<ChatInfoDto>?>,
    isLoadingEnd: MutableState<Boolean>,
    test: MutableState<Boolean>? = null
) {
    if(studyId.toInt() == -1) return
    val apiService = RetrofitInstance.apiService
    val call = apiService.getChatInfoList(studyId)

    apiService.getChatInfoList(studyId).enqueue(object : Callback<List<ChatInfoDto>> {
        override fun onResponse(
            call: Call<List<ChatInfoDto>>,
            response: Response<List<ChatInfoDto>>
        ) {
            isLoadingEnd.value = true
            val chatRoomDtoList = response.body()
            chatInfoDtoList.value = response.body()?.asReversed()
        }

        override fun onFailure(call: Call<List<ChatInfoDto>>, t: Throwable) {
            t.printStackTrace()
            Toast.makeText(context, "채팅방 정보를 받아오는데 실패하였습니다", Toast.LENGTH_SHORT).show()
        }
    })
}


