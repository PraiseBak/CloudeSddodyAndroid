package com.study.sddodyandroid.dto

data class CommentReqestDto (
    var studyId : Long? = null,
    var boardId : Long? = null,
    var content : String
){
    fun isValid(): Boolean {
        return !(content.length < 2 || content.length > 200)
    }

}

