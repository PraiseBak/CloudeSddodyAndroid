package com.study.sddodyandroid.service

import android.content.Context
import androidx.navigation.NavController
import com.google.gson.Gson
import com.study.sddodyandroid.common.AuthorizationCallBack
import com.study.sddodyandroid.common.RetrofitInstance
import com.study.sddodyandroid.dto.MemberDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

fun sendSetMemberInfoRequest(requestDto: MemberDto, navController: NavController, context : Context, profileImage : File?){
    val apiService = RetrofitInstance.apiService

    // MemberDto 객체를 JSON 문자열로 변환 후, RequestBody로 변환
    requestDto.nickname = requestDto._nickname.value
    requestDto.selfIntroduce = requestDto._selfIntroduce.value
    val memberDtoJson = Gson().toJson(requestDto)
    val memberDtoRequestBody = RequestBody.create("application/json".toMediaTypeOrNull(), memberDtoJson)

    var imageBody : MultipartBody.Part? = null
    var requestFile : RequestBody

    // 이미지 파일을 MultipartBody.Part로 변환
    profileImage?.let {
        requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),it)
        imageBody = MultipartBody.Part.createFormData("profileImage", profileImage.name, requestFile)
    }

    val call = apiService.setMemberInfo(memberDtoRequestBody,imageBody)
    call.enqueue(AuthorizationCallBack.createSetMemberInfoCallback(context,navController))
}