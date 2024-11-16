package com.study.sddodyandroid.common

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.study.sddodyandroid.activity.MainActivity
import com.study.sddodyandroid.dto.StudyRequestDto
import com.study.sddodyandroid.activity.StartActivity
import com.study.sddodyandroid.dto.TokenDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object AuthorizationCallBack {

    // API 호출 결과 처리를 위한 커스텀 콜백 생성
    fun <T> createCustomCallback(context: Context): Callback<T> {
        return object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {

                } else if(response.code() == 401){
                    Toast.makeText(context, "토큰이 만료되었습니다 재로그인 해주세요", Toast.LENGTH_SHORT).show()
                    //값 초기화
                    // TODO: 테스트
                    context.getSharedPreferences("KakaoLoginPreferences",Context.MODE_PRIVATE).edit()
                        .clear()
                        .apply()
                    val intent = Intent(context, StartActivity::class.java)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                Toast.makeText(context, "API 호출에 실패하였습니다 다시 시도해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //setMemberInfo의 경우 memberId를 넣어준다
    fun createSetMemberInfoCallback(context: Context, navController: NavController): Callback<TokenDto> {
        return object : Callback<TokenDto> {
            override fun onResponse(call: Call<TokenDto>, response: Response<TokenDto>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    context.getSharedPreferences("KakaoLoginPreferences",Context.MODE_PRIVATE).edit()
                        .putString("memberId", result?.memberId.toString())
                        .putString("nickname",result?.nickname)
                        .apply()
                    navController.navigate("main")
                } else if(response.code() == 401){
                    Log.d("DEBUG","토큰이 만료돠었습니다 재로그인 해주세요")
                    Toast.makeText(context, "토큰이 만료돠었습니다 재로그인 해주세요", Toast.LENGTH_SHORT).show()
                    //값 초기화
                    // TODO: 테스트
                    context.getSharedPreferences("KakaoLoginPreferences",Context.MODE_PRIVATE).edit()
                        .clear()
                        .apply()
                    val intent = Intent(context, StartActivity::class.java)
                    context.startActivity(intent)
                } else {
                    Log.d("DEBUG","권한없음")
                    Toast.makeText(context, "API 호출에 필요한 권한이 없습니다 다시 로그인해주세요", Toast.LENGTH_SHORT).show()
                    context.getSharedPreferences("KakaoLoginPreferences",Context.MODE_PRIVATE).edit()
                        .clear()
                        .apply()
                    val intent = Intent(context, StartActivity::class.java)
                    context.startActivity(intent)
                }
            }

            override fun onFailure(call: Call<TokenDto>, t: Throwable) {
                Log.d("DEBUG","API 호출 실패")
                Toast.makeText(context, "API 호출에 실패하였습니다 다시 시도해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun createStudyInfoCallback(context: Context): Callback<StudyRequestDto> {
        return object : Callback<StudyRequestDto> {
            override fun onResponse(call: Call<StudyRequestDto>, response: Response<StudyRequestDto>) {
                if (response.isSuccessful) {
                    val studyDto = response.body()
                } else if(response.code() == 401){
                    Toast.makeText(context, "토큰이 만료돠었습니다 재로그인 해주세요", Toast.LENGTH_SHORT).show()
                    //값 초기화
                    // TODO: 테스트
                    context.getSharedPreferences("KakaoLoginPreferences",Context.MODE_PRIVATE).edit()
                        .clear()
                        .apply()
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "API 호출에 필요한 권한이 없습니다 다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StudyRequestDto>, t: Throwable) {
                Toast.makeText(context, "API 호출에 실패하였습니다 다시 시도해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }







}