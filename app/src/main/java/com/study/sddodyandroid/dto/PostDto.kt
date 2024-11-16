package com.study.sddodyandroid.dto

import com.study.sddodyandroid.helper.BoardCategoryEnum

data class PostDto(
    val postId: Long,
    val title: String,
    val content: String,
//    val author: MemberDto,
//    val memberDto: MemberDto?,
//    val comment: List<CommentDto>,
)
