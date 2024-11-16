package com.study.sddodyandroid.helper.member

enum class DevYearEnum(val text : String,val yearStr :String = "") {
    BEGINNER("개발을 시작한지 얼마되지 않았어요! (0-2년)","0-2"),
    SKILLED("개발에 익숙해졌어요 (2-5년)","2-5"),
    MASTER("손바닥 안에 있어요 (5년+)","5+"),
}
