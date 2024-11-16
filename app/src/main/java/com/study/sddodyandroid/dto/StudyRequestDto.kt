package com.study.sddodyandroid.dto

import com.google.android.gms.maps.model.LatLng
import com.study.sddodyandroid.helper.DeveloperEnum
import com.study.sddodyandroid.helper.FrameworkEnum
import com.study.sddodyandroid.helper.member.DevLevelEnum
import com.study.sddodyandroid.helper.member.DevYearEnum

data class StudyRequestDto(
    //스터디 번호
//    var studyId: Long?,
    // 스터디 제목
    var title: String,

    // 스터디 내용
    var content: String,

    var maxStudyMemberNum: Int,

    var studyField: List<DeveloperEnum> = arrayListOf(),

    var studyTechStack: List<FrameworkEnum> = arrayListOf(),

    var isOpenToEveryone : Boolean = false,

    var location : LatLng = LatLng(37.3846, 127.1250),
    //개발 수준
    var devLevel : DevLevelEnum,
    //개발 몇년정도했는지
    var devYear : DevYearEnum


)