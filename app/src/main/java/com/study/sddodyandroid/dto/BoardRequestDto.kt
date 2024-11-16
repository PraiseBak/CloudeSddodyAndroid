package com.study.sddodyandroid.dto

import java.io.File

enum class CommunityValidationErrorEnum(val msg : String){
    TITLE_ERROR("제목은 2글자 이상 40자 이하여야 해요"),
    CONTENT_ERROR("내용은 2글자 이상 4000자 이하여야 해요"),
}

data class BoardRequestDto (
    var title: String,

    var content : String,

    var boardTargetStudyId : Long? = null,

    val imageList : ArrayList<File> = arrayListOf(),


    //포트폴리오 여부
    var isPortfolio : Boolean = false,


    //태그 리스트(해시 태그)
    var tagList : List<String> = listOf(),

    var link : String = ""
)
{
    /**
     * 스터디 대상 글일시 무조건 false여야함
     *
     */
    fun setPortfolioFalse() {
        isPortfolio = false
    }


    fun isValid() : String{

        if(title.length < 2 || title.length > 40){
            return CommunityValidationErrorEnum.TITLE_ERROR.msg
        }

        if(content.length < 2 || content.length > 4000){
            return CommunityValidationErrorEnum.CONTENT_ERROR.msg
        }

        return ""
    }

}
