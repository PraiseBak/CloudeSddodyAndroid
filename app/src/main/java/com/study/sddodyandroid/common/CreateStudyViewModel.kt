package com.study.sddodyandroid.common

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.LatLng
import com.study.sddodyandroid.activity.MainViewRoute
import com.study.sddodyandroid.dto.StudyRequestDto
import com.study.sddodyandroid.helper.DeveloperEnum
import com.study.sddodyandroid.helper.FrameworkEnum
import com.study.sddodyandroid.helper.PhotoUriManager
import com.study.sddodyandroid.helper.member.DevLevelEnum
import com.study.sddodyandroid.helper.member.DevYearEnum
import com.study.sddodyandroid.service.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CreateStudyViewModel (
    private val photoUriManager: PhotoUriManager
) : ViewModel() {

    var devLevel: DevLevelEnum = DevLevelEnum.HAVING_FUN
    var devYear: DevYearEnum = DevYearEnum.BEGINNER
    private val questionOrder: List<CreateStudyEnum> = listOf(
        CreateStudyEnum.TITLE_CONTENT,
        CreateStudyEnum.IMAGE,
        CreateStudyEnum.LOCATION,
        CreateStudyEnum.ALLOW_STUDY,
        CreateStudyEnum.REQUIRE_DEVELOPER,
        CreateStudyEnum.REQUIRE_STACK_TECH,
        CreateStudyEnum.MAX_MEMBER
    )


    private var questionIndex = 0

    // ----- Responses exposed as State -----

    //타이틀
    private val _title = mutableStateOf<String>("")
    val title
        get() = _title

    //소개글(body)
    private val _content = mutableStateOf<String>("")
    val content
        get() = _content

    //참가 신청 즉시 or승인
    private val _isOpenToEveryone = mutableStateListOf<Int>()
    val isOpenToEveryone2: List<Int>
        get() = _isOpenToEveryone

    var isOpenToEveryone = mutableStateOf<Boolean>(false)

    var developerEnumList: MutableList<DeveloperEnum> = mutableListOf()


    var frameworkEnumList: MutableList<FrameworkEnum> = mutableListOf()

    //이미지

    var imageUriList = mutableListOf<Uri>()

    var curLocation = LatLng(37.3846, 127.1250)

    private val _location = mutableStateOf<LatLng>(curLocation)
    val location
        get() = _location

    val _maxMember = mutableStateOf<Int>(2)
    val maxMember
        get() = _maxMember
    // ----- Survey status exposed as State -----

    private val _surveyScreenData = mutableStateOf(createSurveyScreenData())
    val createStudyScreenData: CreateStudyScreenData?
        get() = _surveyScreenData.value

    private val _isNextEnabled = mutableStateOf(false)
    val isNextEnabled: Boolean
        get() = _isNextEnabled.value

    /**
     * Returns true if the ViewModel handled the back press (i.e., it went back one question)
     */
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

    fun onNextPressed() {
        changeQuestion(questionIndex + 1)
    }

    private fun changeQuestion(newQuestionIndex: Int) {
        questionIndex = newQuestionIndex
        _isNextEnabled.value = getIsNextEnabled()
        _surveyScreenData.value = createSurveyScreenData()
    }

    fun onDonePressed(onSurveyComplete: () -> Unit, context: Context,
                      navController : NavHostController) {
        val studyRequestDto: StudyRequestDto = getStudyDto()
        val fileList : MutableList<File> = mutableListOf()
        imageUriList.forEach{
            uriToFile(context,it)?.let { file -> fileList.add(file) }
        }
        val imageList: List<MultipartBody.Part> = fileList.mapNotNull { imageUrl ->
            imageUrl?.let {
                val file = it // 이미지 파일 경로로부터 File 객체 생성

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("imageList[]", file.name, requestFile)

                imagePart
            }
        }
        val apiService = RetrofitInstance.apiService
        val call = apiService.createStudy(studyRequestDto,imageList)


        call?.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                onSurveyComplete()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "스터디를 개설에 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
        })

//        navController.navigate(MainViewRoute.MAIN)
    }
    //타이틀 String 몇글자이상, boolean은 제약사항으로 둬야함
    fun onTitleResponse(answer: String) {
        _title.value = answer
        _isNextEnabled.value = getIsNextEnabled()
    }

    //소개글
    fun onContentResponse(content: String) {
        _content.value = content
        _isNextEnabled.value = getIsNextEnabled()
    }

    //참가신청 승인
    fun onIsOpenToEveryoneResponse(selected: Boolean, answer: Int) {
//        if(selected){
//            _isOpenToEveryone.add(answer)
//        }else{
//            _isOpenToEveryone.remove(answer) //가입승인
//        }
        _isNextEnabled.value = true
    }

    //직업
    fun onRequireDeveloperResponse(selected:Boolean, dev:DeveloperEnum) {
        if(selected){
            developerEnumList.add(dev)
        }else{
            var removeIndex = 0
            developerEnumList.forEachIndexed{index,it ->
                if(it.info == dev.info){
                    removeIndex = index
                    return@forEachIndexed
                }
            }

            developerEnumList.removeAt(removeIndex)
        }

        _isNextEnabled.value = developerEnumList.size > 0
    }
    //기술
    fun onRequireStackTech(selected:Boolean, framework: FrameworkEnum){
        if(selected){
            frameworkEnumList.add(framework)
        }else{
            frameworkEnumList.remove(framework)
        }
        _isNextEnabled.value = getIsNextEnabled()
    }
    //이미지
    fun onImgResponse(uri: MutableList<Uri>) {
        imageUriList = uri
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun onLocation(location: LatLng){
        _location.value = location
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun onMaxMember(member: Int) {
        maxMember.value = member
        _isNextEnabled.value = getIsNextEnabled()
    }
    fun getNewSelfieUri() = photoUriManager.buildNewUri()

    private fun getIsNextEnabled(): Boolean {
        return when (questionOrder[questionIndex]) {
//            SurveyQuestion.FREE_TIME -> _freeTimeResponse.value.length != 0
            CreateStudyEnum.TITLE_CONTENT -> true
//            SurveyQuestion.TAKE_SELFIE -> imageUriList.size != 0
            CreateStudyEnum.IMAGE -> true
            CreateStudyEnum.LOCATION -> true
            CreateStudyEnum.ALLOW_STUDY -> _isOpenToEveryone.isNotEmpty()
            CreateStudyEnum.REQUIRE_DEVELOPER -> developerEnumList.isNotEmpty()
            CreateStudyEnum.REQUIRE_STACK_TECH -> frameworkEnumList.isNotEmpty()
            CreateStudyEnum.MAX_MEMBER -> _maxMember.value > 1
        }
    }

    private fun createSurveyScreenData(): CreateStudyScreenData {
        return CreateStudyScreenData(
            questionIndex = questionIndex,
            questionCount = questionOrder.size,
            shouldShowPreviousButton = questionIndex > 0,
            shouldShowDoneButton = questionIndex == questionOrder.size - 1,
            createStudyEnum = questionOrder[questionIndex],
        )
    }


    fun getStudyDto(): StudyRequestDto{
        val studyRequestDto = StudyRequestDto(
            location = location.value,
            title = title.value,
            content = content.value,
            maxStudyMemberNum = maxMember.value,
            studyField = developerEnumList,
            studyTechStack = frameworkEnumList,
            isOpenToEveryone = isOpenToEveryone.value,
            devLevel = devLevel,
            devYear = devYear
        )
        return studyRequestDto
    }
}
class CreateStudyViewModelFactory(
    private val photoUriManager: PhotoUriManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateStudyViewModel::class.java)) {
            return CreateStudyViewModel(photoUriManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
enum class CreateStudyEnum {
    TITLE_CONTENT,
    ALLOW_STUDY,
    REQUIRE_DEVELOPER,
    REQUIRE_STACK_TECH,
    IMAGE,
    MAX_MEMBER,
    LOCATION
}

data class CreateStudyScreenData(
    val questionIndex: Int,
    val questionCount: Int,
    val shouldShowPreviousButton: Boolean,
    val shouldShowDoneButton: Boolean,
    val createStudyEnum: CreateStudyEnum,
)