package com.study.sddodyandroid.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import com.study.sddodyandroid.common.RetrofitInstance
import com.study.sddodyandroid.common.StudyAdminViewModel
import com.study.sddodyandroid.dto.MemberInfoDto
import com.study.sddodyandroid.dto.PageDto
import com.study.sddodyandroid.dto.RequestParticipationRequestDto
import com.study.sddodyandroid.dto.StudyIdMemberIdDto
import com.study.sddodyandroid.dto.StudyRequestDto
import com.study.sddodyandroid.dto.StudyResponseDto
import com.study.sddodyandroid.helper.study.StudyOrderType
import com.study.sddodyandroid.helper.study.StudyStateEnum
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

fun sendStudyCreateRequest(studyRequestDto : StudyRequestDto,
                           sendImageList: MutableState<MutableList<File>>,
                           context : Context,
                           navController: NavHostController
){
    val apiService = RetrofitInstance.apiService

    val imageList: List<MultipartBody.Part> = sendImageList.value.map { imageUrl ->
        imageUrl.let {
            val file = it // 이미지 파일 경로로부터 File 객체 생성
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("imageList[]", file.name, requestFile)
            imagePart
        }
    }

    val call = apiService.createStudy(studyRequestDto,imageList)
    call?.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            navController.navigate("mainView")
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Toast.makeText(context, "글쓰기에 실패하였습니다", Toast.LENGTH_SHORT).show()
        }
    })

}


fun getMemberStudyList(
    memberId : Long,
    context: Context,
    rememberStudyList: MutableState<MutableList<StudyResponseDto>>,
    rememberIsLoading: MutableState<Boolean>,
    orderType: StudyOrderType = StudyOrderType.RECENT_CREATE,
){
    val apiService = RetrofitInstance.apiService
    val call = apiService.getMemberStudyList(memberId,orderType)
    call?.enqueue(object : Callback<List<StudyResponseDto>> {
        override fun onResponse(
            call: Call<List<StudyResponseDto>>,
            response: Response<List<StudyResponseDto>>
        ) {
            response.body()?.let {
                var sumMutableList :MutableList<StudyResponseDto> =  mutableListOf()
                sumMutableList.addAll(it)
                rememberStudyList.value = sumMutableList
                rememberIsLoading.value = true
            }
        }

        override fun onFailure(call: Call<List<StudyResponseDto>>, t: Throwable) {

            t.message?.let { Log.e("Sddody", it) };
        }
    })
}

fun getStudyList(
    pageDto: PageDto,
    context: Context,
    rememberStudyList: MutableState<MutableList<StudyResponseDto>>,
    rememberIsLoading: MutableState<Boolean>,
    orderType: StudyOrderType,
){
    val apiService = RetrofitInstance.apiService
    val call = apiService.getStudyList(pageDto.page,orderType)
    call?.enqueue(object : Callback<List<StudyResponseDto>> {
        override fun onResponse(
            call: Call<List<StudyResponseDto>>,
            response: Response<List<StudyResponseDto>>
        ) {
            response.body()?.let {
                var sumMutableList :MutableList<StudyResponseDto> =  mutableListOf()
                sumMutableList.addAll(it)
//                sumMutableList.addAll(rememberStudyList.value)
                rememberStudyList.value = sumMutableList
                rememberIsLoading.value = true
            }
        }

        override fun onFailure(call: Call<List<StudyResponseDto>>, t: Throwable) {
        }
    })
}

fun getStudy(
    id: Long,
    context: Context,
    rememberStudy: MutableState<StudyResponseDto?>,
    rememberIsLoading: MutableState<Boolean>,
    studyAdminViewModel: StudyAdminViewModel? = null
){
    val apiService = RetrofitInstance.apiService
    val call = apiService.getStudy(id)
    call?.enqueue(object : Callback<StudyResponseDto> {

        override fun onResponse(
            call: Call<StudyResponseDto>,
            response: Response<StudyResponseDto>
        ) {
            if (response.isSuccessful) {
                response.body()?.let {
                    rememberStudy.value = it
                    studyAdminViewModel?.updateStudyDto(it)
                }
                rememberIsLoading.value = true
            }
        }

        override fun onFailure(call: Call<StudyResponseDto>, t: Throwable) {
        }
    })

}

fun setRecommendStudy(context: Context, rememberStudyList: MutableState<MutableList<StudyResponseDto>>) {
    val apiService = RetrofitInstance.apiService
    val call = apiService.getRecommendStudy()
    call?.enqueue(object : Callback<List<StudyResponseDto>> {
        override fun onResponse(
            call: Call<List<StudyResponseDto>>,
            response: Response<List<StudyResponseDto>>
        ) {
            if (response.isSuccessful) {
                response.body()?.let {
                    rememberStudyList.value = it.toMutableList()
                }
            }
        }

        override fun onFailure(call: Call<List<StudyResponseDto>>, t: Throwable) {
        }
    })
}


//참여 신청한 멤버 리스트
fun getRequestMemberList(
    context : Context,
    studyId: Long,
    rememberRequestMemberList: MutableState<List<MemberInfoDto>?>
) {
    val apiService = RetrofitInstance.apiService
    val call = apiService.getRequestMemberList(studyId)

    call?.enqueue(object : Callback<List<MemberInfoDto>> {
        override fun onResponse(call: Call<List<MemberInfoDto>>, response: Response<List<MemberInfoDto>>) {
            if (response.isSuccessful ) {
                val memberDtoList = response.body()
                rememberRequestMemberList.value = memberDtoList
            } else {
                Toast.makeText(context, "유저 정보를 가져오는데 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<List<MemberInfoDto>>, t: Throwable) {
            Toast.makeText(context, "유저 정보를 가져오는데 실패하였습니다", Toast.LENGTH_SHORT).show()
        }
    })

}


fun adminStudyCheckParticipation(isAccept : Boolean
                                 ,requestMemberId : Long,
                                 studyId : Long,
                                 context : Context,
                                 navController: NavHostController){
    val apiService = RetrofitInstance.apiService
    val call = apiService.studyCheckParticipation(studyId,
        RequestParticipationRequestDto(
            requestMemberId,
            isAccept
        ))

    call?.enqueue(object : Callback<Void>{
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if(response.isSuccessful){
                navController.navigate("study/${studyId}/admin")
            }else{
                Toast.makeText(context, "스터디 참여신청을 결정하는데 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
        }
        
        override fun onFailure(call: Call<Void>, t: Throwable) {
            Toast.makeText(context, "스터디 참여신청을 결정하는데 실패하였습니다", Toast.LENGTH_SHORT).show()
        }
    })
}


fun studyParticipationRequest(
    studyId: Long,
    context: Context,
    message : String = ""){

    val apiService = RetrofitInstance.apiService
    val call = apiService.studyParticipationRequest(studyId, message)
    call?.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if(response.isSuccessful){
                Toast.makeText(context, "참여 신청했습니다", Toast.LENGTH_SHORT).show()
            }else if(response.code() == 400){
                Toast.makeText(context, "이미 참여 신청한 스터디입니다", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(context, "${response.code()} 신청에 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Toast.makeText(context, "스터디에 참여할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    })

}


fun studyMemberDelete(
    studyIdMemberIdDto: StudyIdMemberIdDto,
    navController: NavHostController,
    context: Context
){
    val apiService = RetrofitInstance.apiService
    val call = apiService.studyDeleteMember(studyIdMemberIdDto)

    call?.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if(response.code() == 200){
                navController.navigate("study/${studyIdMemberIdDto.studyId}/admin")
            }else{
                Toast.makeText(context, "요청에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Toast.makeText(context, "요청에 실패하였습니다.", Toast.LENGTH_SHORT).show()

        }
    })


}

fun studyMemberRefuse(
    studyIdMemberIdDto: StudyIdMemberIdDto,
    navController: NavHostController,
    context: Context
){
    val apiService = RetrofitInstance.apiService
    val call = apiService.studyMemberRefuse(studyIdMemberIdDto)

    call?.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if(response.code() == 200){
                navController.navigate("study/${studyIdMemberIdDto.studyId}")
            }else{
                Toast.makeText(context, "요청에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Toast.makeText(context, "요청에 실패하였습니다.", Toast.LENGTH_SHORT).show()

        }
    })
}
/**
 * 열려있는 스터디 좌표 리스트를 가져옵니다
 */
fun getStudyLocationInfoList(
    context: Context,
    rememberStudyLocationList: MutableState<MutableList<StudyResponseDto>>,
    //주변의 추천 스터디만 가져올지 여부
    isOnlyNearRecommend : Boolean = false
){
    val apiService = RetrofitInstance.apiService
    val call = apiService.getStudyLocationInfoList(isOnlyNearRecommend)
    call.enqueue(object : Callback<List<StudyResponseDto>>{

        override fun onResponse(
            call: Call<List<StudyResponseDto>>,
            response: Response<List<StudyResponseDto>>
        ) {
            if (response.isSuccessful) {
                response.body()?.let {
                    rememberStudyLocationList.value = it.toMutableList()
                }
            }
        }

        override fun onFailure(call: Call<List<StudyResponseDto>>, t: Throwable) {
        }
    })


}

/*
스터디 시작 혹은 종료
 */
fun updateStudyStatus(studyId: Long, newStatus: StudyStateEnum, navController: NavHostController, context: Context) {
    val apiService = RetrofitInstance.apiService
    val call = apiService.updateStudyStatus(studyId,newStatus)
    call?.enqueue(object : Callback<Void>{
        override fun onResponse(
            call: Call<Void>,
            response: Response<Void>
        ) {
            if (response.isSuccessful) {
                navController.navigate("study/${studyId}")
                Toast.makeText(context, "스터디 상태를 ${newStatus.text}로 바꿨어요!", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
        }
    })
}

