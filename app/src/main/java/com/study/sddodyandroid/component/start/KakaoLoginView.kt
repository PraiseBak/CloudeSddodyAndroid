package com.study.sddodyandroid.component.start
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.study.sddodyandroid.R
import com.study.sddodyandroid.common.RetrofitInstance
import com.study.sddodyandroid.component.start.Destinations.MEMBER_INFO_ROUTE
import com.study.sddodyandroid.dto.TokenDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun KakaoLoginView(context: Context, onLogin: (Boolean) -> Unit, navController: NavHostController) {
    val sharedPreferences = context.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE)
    val isNotLogged = remember {mutableStateOf(sharedPreferences.getString("accessToken", null) == null)}

    val KAKAO_SDK = context.getString(R.string.KAKAO_SDK)
    KakaoSdk.init(context,KAKAO_SDK)


    fun sendSignupRequestToken(context: Context, sharedPreferences: SharedPreferences, token: OAuthToken) {
        val tokenDto = TokenDto(
            accessToken = token.accessToken,
        )

        val apiService = RetrofitInstance.apiService
        val call = apiService.signup(tokenDto)
        call?.enqueue(object : Callback<TokenDto> {
            override fun onResponse(
                call: Call<TokenDto>,
                response: Response<TokenDto>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val editor = sharedPreferences.edit()
                    editor.putString("accessToken", result?.accessToken)
                    editor.putString("refreshToken", result?.refreshToken)
                    result?.memberId?.let {
                        editor.putString("memberId", it.toString())
                        editor.putString("nickname", result.nickname)
                        editor.putBoolean("isDeleted", result.isDeleted)
                    }
                    editor.apply()
                    when {
                        result?.isDeleted == true -> {
                            navController.navigate(Destinations.WELCOME_ROUTE)
                        }
                        result?.memberId != null -> {
                            navController.navigate(Destinations.MAIN)
                        }

                        else -> {
                            navController.navigate(MEMBER_INFO_ROUTE)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<TokenDto>, t: Throwable) {
                println(t.message)
                println(t.printStackTrace())
                println("응답실패")
            }
        })
    }

    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.d("debug","error " + error.toString())
            // Error handling
        } else if (token != null && token.idToken != null) {
            Log.d("debug","send token")

            sendSignupRequestToken(context, context.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE), token)
            // Recreate activity if needed
        } else {
            Log.d("debug","another case")
            // Handle other cases
        }
    }

    fun kakaoLogin(context: Context, sharedPreferences: SharedPreferences) {
        sharedPreferences.edit().clear().apply()
        if (sharedPreferences.getString("accessToken", null) != null) {
            return
        }

         if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    println("error = $error")
                } else if (token != null) {
                    sendSignupRequestToken(context, sharedPreferences, token)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedButton(
            onClick = {
                if (isNotLogged.value) {

                    kakaoLogin(context, sharedPreferences)
                } else {
                    kakaoLogout(context, sharedPreferences,isNotLogged)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp)
                .padding(top = 20.dp, bottom = 24.dp),
        ) {
            if(isNotLogged.value){
                Text(text = "카카오로 시작하기")
            }else{
                Text(text = "로그아웃")
            }
        }
    }
}

fun kakaoLogout(context: Context, sharedPreferences: SharedPreferences, isNotLogged: MutableState<Boolean>? = null) {
    sharedPreferences.edit().clear().apply()

    isNotLogged?.let {
        it.value = true
    }
}