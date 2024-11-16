package com.study.sddodyandroid.helper.member

enum class MemberStatus (
    val status : Int,
    val msg : String
){
    ROLE_INFO_VALID(100,"유효한 유저입니다"),
    ROLE_INFO_NOT_VALID(101,"아직 정보가 덜 입력 됐어요"),
    ROLE_BLOCKED(102,"차단된 유저입니다"),
    ROLE_DELETED(404,"비활성화 된 유저입니다")
}