package com.study.sddodyandroid.dto

class TokenDto(
    var accessToken: String? = null,
    var refreshToken :String? = null,
    var memberId : Long? = null,
    val nickname: String? = null,
    val isDeleted : Boolean = false
) {
}
