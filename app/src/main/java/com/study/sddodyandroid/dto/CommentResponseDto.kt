package com.study.sddodyandroid.dto

import java.util.Date

data class CommentResponseDto (
    var id : Long,
    var content : String,
    var nickname : String,
    var userProfileImageSrc : String,
    var createdAt : Date,
    var updatedAt: Date,
){
}
