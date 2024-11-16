package com.study.sddodyandroid.dto

import java.time.LocalDateTime
import java.util.Date


data class ChatInfoDto(
    val msg: String,

    val imageSrcList: List<String> = arrayListOf(),

    val writeMemberNickname: String,

    val readCount: Long,

    var isDeleted: Boolean,

    val writeMemberProfileSrc: String?,

    val createdDate: Date?,

    val writeMemberId: Long
) {


}