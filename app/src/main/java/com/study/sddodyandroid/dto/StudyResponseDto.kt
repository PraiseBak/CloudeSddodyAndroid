package com.study.sddodyandroid.dto

import com.google.android.gms.maps.model.LatLng
import com.study.sddodyandroid.helper.DeveloperEnum
import com.study.sddodyandroid.helper.FrameworkEnum
import com.study.sddodyandroid.helper.member.DevLevelEnum
import com.study.sddodyandroid.helper.member.DevYearEnum
import com.study.sddodyandroid.helper.study.StudyStateEnum
import java.util.Date

data class StudyResponseDto (
    //스터디 번호
    var studyId: Long,

    // 스터디 제목
    var title: String,

    // 스터디 내용
    var content: String,

    var maxStudyMemberNum: Int,

    var studyImageSrcList: List<String>,

    var studyField: List<DeveloperEnum> = arrayListOf(),

    var studyTechStack: List<FrameworkEnum> = arrayListOf(),

    var isOpenToEveryone : Boolean = false,

    var heartCount : Int,

    var studyMemberInfoDtoList : List<MemberInfoDto> = listOf(),
    var chatRoomResponseDto: ChatRoomDto,

    var createdAt : Date,

    var updatedAt: Date,

    val location : LatLng,

    var devYear: DevYearEnum,

    var devLevel: DevLevelEnum,

    var ownerMemberInfo : MemberInfoDto,

    var studyStateEnum: StudyStateEnum,
    ){

}

