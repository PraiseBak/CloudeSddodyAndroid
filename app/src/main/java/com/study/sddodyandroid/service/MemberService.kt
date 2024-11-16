package com.study.sddodyandroid.service

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import com.study.sddodyandroid.activity.MainViewRoute
import com.study.sddodyandroid.common.CreateStudyViewModel
import com.study.sddodyandroid.common.RetrofitInstance
import com.study.sddodyandroid.dto.MemberInfoDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun changeMemberInfo(context : Context,navController: NavController,
                     onEnd : () -> Unit = {
                         navController.navigate("${MainViewRoute.PROFILE}/0")
                     }){
    val apiService = RetrofitInstance.apiService
    val call = apiService.changeMemberStatus()
    call.enqueue(object  : Callback<Void>{
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if(response.isSuccessful){
                onEnd()
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Toast.makeText(context, "유저 상태를 변경하는데 실패하였습니다", Toast.LENGTH_SHORT).show()
        }

    })

}

fun setMyMemberInfo(context: Context, viewModel: CreateStudyViewModel){
    val sharedPreferences = context.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE)
    val memberId = sharedPreferences.getString("memberId", null)

    memberId?.let { memberId ->
        // Retrofit 인터페이스에서 memberId를 파라미터로 사용하는 메서드를 호출합니다.
        val apiService = RetrofitInstance.apiService
        val call = apiService.getMemberInfo(memberId.toLong())

        call?.enqueue(object : Callback<MemberInfoDto> {
            override fun onResponse(call: Call<MemberInfoDto>, response: Response<MemberInfoDto>) {
                if (response.isSuccessful ) {
                    val memberDto = response.body()
                    if(memberDto != null){
                        // 사용자 정보를 받아온 후에 처리하는 로직을 구현합니다.
                        viewModel.devYear = memberDto.devYearEnum
                        viewModel.devLevel = memberDto.devLevelEnum
                        viewModel.developerEnumList = memberDto.developerEnumList.toMutableList()
                        viewModel.frameworkEnumList = memberDto.frameworkEnumList.toMutableList()
                    }
                } else {
                    Toast.makeText(context, "유저 정보를 가져오는데 실패하였습니다", Toast.LENGTH_SHORT).show()
                    // 서버 요청이 실패한 경우 처리합니다.
                }
            }

            override fun onFailure(call: Call<MemberInfoDto>, t: Throwable) {
                // 네트워크 오류 등 요청이 실패한 경우 처리합니다.
            }
        })
    }
}

fun getMemberInfo(context: Context, rememberMemberInfo: MutableState<MemberInfoDto?>, memberId : Long,
                  rememberIsLoading : MutableState<Boolean> ?= null){
    val apiService = RetrofitInstance.apiService
    val call = apiService.getMemberInfo(memberId)

    call?.enqueue(object : Callback<MemberInfoDto> {
        override fun onResponse(call: Call<MemberInfoDto>, response: Response<MemberInfoDto>) {
            if (response.isSuccessful ) {
                rememberMemberInfo.value = response.body()
                rememberIsLoading?.value = true
                println(rememberMemberInfo)
            } else {
                Toast.makeText(context, "유저 정보를 가져오는데 실패하였습니다", Toast.LENGTH_SHORT).show()
                // 서버 요청이 실패한 경우 처리합니다.
            }
        }

        override fun onFailure(call: Call<MemberInfoDto>, t: Throwable) {
            Toast.makeText(context, "유저 정보를 가져오는데 실패하였습니다", Toast.LENGTH_SHORT).show()
        }
    })

}