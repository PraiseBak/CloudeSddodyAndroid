package com.study.sddodyandroid.dto

import com.google.gson.annotations.SerializedName

data class GithubUserProfileDto(
    @SerializedName("name")
    val nickname : String?,
    @SerializedName("public_repos")
    val publicRepos : Int,
    val followers : Int
) {


}
