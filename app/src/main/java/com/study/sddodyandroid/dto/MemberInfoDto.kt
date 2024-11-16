package com.study.sddodyandroid.dto

import com.google.android.gms.maps.model.LatLng
import com.study.sddodyandroid.helper.DeveloperEnum
import com.study.sddodyandroid.helper.FrameworkEnum
import com.study.sddodyandroid.helper.LanguageEnum
import com.study.sddodyandroid.helper.member.DevLevelEnum
import com.study.sddodyandroid.helper.member.DevYearEnum
import com.study.sddodyandroid.helper.member.MemberStatus


data class MemberInfoDto(
    val id : Long,
    val nickname : String,
    val selfIntroduce: String,
    val memberProfileImgSrc : String,
    val devYearEnum : DevYearEnum,
    val devLevelEnum: DevLevelEnum,
    val frameworkEnumList: List<FrameworkEnum>,
    val developerEnumList: List<DeveloperEnum>,
    val devLanguageEnumList : List<LanguageEnum>,
    val githubNickname : String?,
    val location : LatLng,
    var memberStatus : MemberStatus
) {

}

