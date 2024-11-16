package com.study.sddodyandroid.common

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONObject

class HeaderInterceptor(
    private val sharedPreferences: SharedPreferences,
    val context: Context,
) : Interceptor {

    @Synchronized
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = sharedPreferences.getString("accessToken", null)
        val refreshToken = sharedPreferences.getString("refreshToken", null)

        val url = chain.call().request().url.toString()
        if(!(url.contains("/api/auth/start"))){
            if(accessToken == null){
                return createErrorResponse(url) // 응답을 생성하여 반환
            }
        }

        var request = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        var response = chain.proceed(request)

        // 만약 인증 에러가 발생하고, refreshToken이 있다면
        if (!response.isSuccessful && response.code == 401 && refreshToken != null) {
            response.close() // 이전 응답을 닫음

            println("refresh token 동작")
            // 토큰 갱신 로직을 수행하고 새로운 accessToken을 받아옵니다.
            response = getNewAccessToken(refreshToken, originalRequest, chain) // 토큰 갱신하는 함수 호출

            //refresh token으로 시도
            if(response.code != 401){
                response = requestWithNewToken(response,originalRequest,chain,context)
            }else{
            }
        }

        return response
    }

    private fun createErrorResponse(url : String): Response {
        val responseBodyJson = "{\"error\": \"유효하지 않은 엑세스 토큰입니다\"}"
        val responseBody = ResponseBody.create("application/json".toMediaTypeOrNull(), responseBodyJson)

        return Response.Builder()
            .code(401)
            .request(Request.Builder().url(url).build())
            .message("유효하지않은 엑세스토큰입니다")
            .body(responseBody)
            .protocol(Protocol.HTTP_1_1)
            .build()
    }
    private fun requestWithNewToken(response: Response,originalRequest: Request,chain: Interceptor.Chain,context : Context): Response {
        val responseData = response.body?.string() // JSON 형태의 데이터를 String으로 받음
        val jsonObject = JSONObject(responseData)
        val edit = sharedPreferences.edit()

        if(!jsonObject.isNull("accessToken")){
            edit.putString("accessToken", jsonObject.getString("accessToken"))
            edit.putString("refreshToken", jsonObject.getString("refreshToken"))
            edit.apply()
            response.close()
            val newAccessToken = jsonObject.getString("accessToken")
            // 새로운 accessToken으로 요청을 재시도합니다.
            val request = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $newAccessToken")
                .build()
            return chain.proceed(request)
        }else{
            sharedPreferences.edit().clear().apply()
        }

        return response
    }

    private fun getNewAccessToken(
        refreshToken: String,
        originalRequest: Request,
        chain: Interceptor.Chain,
    ): Response {
        val request = originalRequest.newBuilder()
            .addHeader("X-REFRESH-TOKEN", "$refreshToken")
            .build()

        return chain.proceed(request)
    }
}
