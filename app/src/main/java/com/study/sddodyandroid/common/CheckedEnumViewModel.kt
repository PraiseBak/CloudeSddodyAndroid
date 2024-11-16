package com.study.sddodyandroid.common

// CheckedEnumViewModel.kt

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.kakao.sdk.friend.m.r
import com.study.sddodyandroid.dto.MemberDto
import com.study.sddodyandroid.dto.MemberInfoDto
import com.study.sddodyandroid.dto.StudyRequestDto
import com.study.sddodyandroid.helper.CheckedEnum
import com.study.sddodyandroid.helper.DeveloperEnum
import com.study.sddodyandroid.helper.FrameworkEnum
import com.study.sddodyandroid.helper.LanguageEnum
import com.study.sddodyandroid.helper.member.DevLevelEnum
import com.study.sddodyandroid.helper.member.DevYearEnum
import com.study.sddodyandroid.helper.getLanguageEnumFromString
import com.study.sddodyandroid.service.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CheckedEnumViewModel : ViewModel() {
    var rememberDevLevel: MutableState<DevLevelEnum> = mutableStateOf(DevLevelEnum.HAVING_FUN)
    var rememberDevYear: MutableState<DevYearEnum> = mutableStateOf(DevYearEnum.BEGINNER)


    private val membershipOrder: List<CreateMemberEnum> = listOf(
        CreateMemberEnum.TITLE_CONTENT,
        CreateMemberEnum.LANGUAGE,
        CreateMemberEnum.DEVELOPER,
        CreateMemberEnum.FRAMEWORK,
        CreateMemberEnum.LOCATION
    )

    private var membershipIndex = 0

    var sharedData: String = ""


    var selectedList: MutableState<MutableList<CheckedEnum>> = mutableStateOf(mutableListOf())
        private set

    var languageList: MutableState<MutableList<CheckedEnum>> = mutableStateOf(mutableListOf())
        private set

    var developerList: MutableState<MutableList<CheckedEnum>> = mutableStateOf(mutableListOf())
        private set

    var frameworkList: MutableState<MutableList<CheckedEnum>> = mutableStateOf(mutableListOf())
        private set


    var selfIntroduce = mutableStateOf<String>("")
    var nickname = mutableStateOf<String>("")
    var profileUri : Uri? = null
    var profileImg : File? = null
    var curLocation = LatLng(37.3846, 127.1250)

    var savedLocation : MutableState<LatLng> = mutableStateOf(curLocation)

    var questionIndex = 0

    private val _isNextEnabled = mutableStateOf(false)
    val isNextEnabled: Boolean
        get() = _isNextEnabled.value

    fun onBackPressed(): Boolean {
        if (questionIndex == 0) {
            return false
        }
        changeQuestion(questionIndex - 1)
        return true
    }

    fun onPreviousPressed() {
        if (questionIndex == 0) {
            throw IllegalStateException("onPreviousPressed when on question 0")
        }
        changeQuestion(questionIndex - 1)
    }

    private fun changeQuestion(newQuestionIndex: Int) {
        questionIndex = newQuestionIndex
    }

    fun onNextPressed() {
        changeQuestion(questionIndex + 1)
    }

    fun onDonePressed(onSurveyComplete: () -> Unit, context: Context) {

    }

    fun onNickNameResponse(answer: String, self: String) {
        nickname.value = answer
        selfIntroduce.value = self
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun onLanguageResponse(selected:Boolean, type: String, item: CheckedEnum){
        if(selected){
            addItemToSelectedList(type,item)
        }else{
            removeItemFromList(type,item)
        }
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun onDeveloperResponse(selected:Boolean, type: String, item: CheckedEnum){
        if(selected){
            addItemToSelectedList(type,item)
        }else{
            removeItemFromList(type,item)
        }
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun onFrameworkResponse(selected:Boolean, type: String, item: CheckedEnum){
        if(selected){
            addItemToSelectedList(type,item)
        }else{
            removeItemFromList(type,item)
        }
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun onLocationResponse(location : LatLng? = null){
        if (location != null){
            savedLocation.value = location
        }
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun setTypeToList(type:String):MutableList<CheckedEnum>{
        val list: MutableList<CheckedEnum> = mutableListOf()
        if(type.equals("language")){
            return languageList.value
        }else if(type.equals("developer")){
            return developerList.value
        }else if(type.equals("framework")){
            return frameworkList.value
        }
        return list
    }
    fun addItemToSelectedList(type: String, item: CheckedEnum, location : LatLng? = null) {
        if(type.equals("language")){languageList.value.add(item)}
        if(type.equals("developer")){developerList.value.add(item)}
        if(type.equals("framework")){frameworkList.value.add(item)}
        if(type == "location" && location != null){
            savedLocation.value = location
        }

    }
    fun removeItemFromList(type: String, item: CheckedEnum) {
        val list = setTypeToList(type)
        for (i in list.indices) {
            if (list[i].infoEnum == item.infoEnum) {
                list.removeAt(i)
                break
            }
        }
    }


    fun getMemberDto(): MemberDto {
        val languageEnumList: List<LanguageEnum> = languageList.value.map { it.infoEnum as LanguageEnum }
        val developerEnumList: List<DeveloperEnum> = developerList.value.map { it.infoEnum as DeveloperEnum }
        val frameworkEnumList: List<FrameworkEnum> = frameworkList.value.map { it.infoEnum as FrameworkEnum }

        val profileUriSrc: String = profileImg.toString()
        return MemberDto(
            nickname = nickname.value,
            selfIntroduce = selfIntroduce.value,
            _nickname = nickname,
            _selfIntroduce = selfIntroduce,
            devLanguageField = languageEnumList,
            interestField = developerEnumList,
            haveExperienceTechStack = frameworkEnumList,
            memberLocation = curLocation,
            profileImg = profileUriSrc,
            devYear = rememberDevYear.value,
            devLevel = rememberDevLevel.value
        )
    }

    private fun getIsNextEnabled(): Boolean {
        return when (membershipOrder[questionIndex]) {

            CreateMemberEnum.TITLE_CONTENT -> nickname.value.length in 2..20  && selfIntroduce.value.length in 2..50
            CreateMemberEnum.LANGUAGE -> languageList.value.size != 0
            CreateMemberEnum.DEVELOPER -> developerList.value.size != 0
            CreateMemberEnum.FRAMEWORK -> frameworkList.value.size != 0
            CreateMemberEnum.LOCATION -> true

        }
    }

    fun setMemberInfo(it: MemberInfoDto) {
        selfIntroduce.value = it.selfIntroduce
        nickname.value = it.nickname
//        profileImg = it.memberProfileImgSrc
//        profileImg = null // 아직 어떻게 제공되는지에 따라 할당
        curLocation = it.location

        rememberDevLevel.value = it.devLevelEnum
        rememberDevYear.value = it.devYearEnum

        this.languageList.value = it.devLanguageEnumList.map { CheckedEnum(infoEnum = it,isChecked = true)}.toMutableList()
        this.frameworkList.value = it.frameworkEnumList.map {CheckedEnum(infoEnum = it,isChecked = true)}.toMutableList()
        this.developerList.value = it.developerEnumList.map { CheckedEnum(infoEnum = it,isChecked = true)}.toMutableList()
    }
}

enum class CreateMemberEnum {
    TITLE_CONTENT,
    LANGUAGE,
    DEVELOPER,
    FRAMEWORK,
    LOCATION
}