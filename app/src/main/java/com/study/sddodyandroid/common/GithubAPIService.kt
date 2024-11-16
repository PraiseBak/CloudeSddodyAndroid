package com.study.sddodyandroid.common

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.MutableState
import com.study.sddodyandroid.activity.MainActivity
import com.study.sddodyandroid.activity.StartActivity
import com.study.sddodyandroid.dto.GithubUserProfileDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface GitHubApiService {
    @GET("user")
    fun getUserProfile(@Header("Authorization") token: String): Call<GithubUserProfileDto>?

    @GET("users/{username}")
    fun getUserProfileByUsername(@Path("username") username: String): Call<GithubUserProfileDto>?
}

object GitHubApi {
    private const val BASE_URL = "https://api.github.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: GitHubApiService = retrofit.create(GitHubApiService::class.java)
}

fun getUserProfileDtoByToken(token: String, context : Context) {
    try {
        val call = GitHubApi.apiService.getUserProfile("Bearer $token")
        call?.enqueue(object : Callback<GithubUserProfileDto> {
            override fun onResponse(
                call: Call<GithubUserProfileDto>,
                response: Response<GithubUserProfileDto>
            ) {
                response.body()?.let {
                    setGithubMemberInfo(it,context)
                }
            }

            override fun onFailure(call: Call<GithubUserProfileDto>, t: Throwable) {
                Toast.makeText(context, "깃허브와 연동하는데 실패하였습니다", Toast.LENGTH_SHORT).show()
            }

        })

        // 응답에서 사용자 프로필 정보 처리
    } catch (e: Exception) {
        // 오류 처리
    }
}


fun getGithubUserProfileByName(
    context: Context,
    username: String,
    rememberGithubUserProfile: MutableState<GithubUserProfileDto?>
) {
    try {
        val call = GitHubApi.apiService.getUserProfileByUsername(username)
        call?.enqueue(object : Callback<GithubUserProfileDto> {
            override fun onResponse(
                call: Call<GithubUserProfileDto>,
                response: Response<GithubUserProfileDto>
            ) {
                if(response.isSuccessful){
                    rememberGithubUserProfile.value = response.body()
                }else{
                    rememberGithubUserProfile.value = GithubUserProfileDto(username,-1,-1)
                }

            }

            override fun onFailure(call: Call<GithubUserProfileDto>, t: Throwable) {
                Toast.makeText(context, "깃허브와 연동하는데 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
        })

    } catch (e: Exception) {

    }
}



fun setGithubMemberInfo(githubUserProfileDto: GithubUserProfileDto,context : Context) {
    val apiService = RetrofitInstance.apiService
    val call = apiService.setGithubMemberInfo(githubUserProfileDto)

    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if(response.isSuccessful){
                if(githubUserProfileDto.nickname != null){
                    Toast.makeText(context, "깃허브와 연동하는데 성공하였습니다.", Toast.LENGTH_SHORT).show()
                }

                val intent = Intent(context, MainActivity::class.java).apply {
                    putExtra("isMemberRedirect", true)
                }
                context.startActivity(intent)
            }else{
                Toast.makeText(context, "깃허브와 연동하는데 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Toast.makeText(context, "깃허브와 연동하는데 실패하였습니다", Toast.LENGTH_SHORT).show()
        }

    })
}
