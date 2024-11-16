package com.study.sddodyandroid.ui.component.main.profile

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.common.HeartDto
import com.study.sddodyandroid.component.chatroom.LoadingIndicator
import com.study.sddodyandroid.component.community.CommentArea
import com.study.sddodyandroid.component.community.CommunityContentCard
import com.study.sddodyandroid.component.community.CommunityContentInputArea
import com.study.sddodyandroid.component.community.CommunityTextArea
import com.study.sddodyandroid.component.community.ExpandableDropdownMenu
import com.study.sddodyandroid.component.community.ExpandableText
import com.study.sddodyandroid.component.community.InputType
import com.study.sddodyandroid.component.conversation.AsyncImageWithHeader
import com.study.sddodyandroid.dto.BoardResponseDto
import com.study.sddodyandroid.dto.CommentReqestDto
import com.study.sddodyandroid.dto.PageDto
import com.study.sddodyandroid.service.getMemberBoardList
import com.study.sddodyandroid.service.sendAddCommentRequest
import com.study.sddodyandroid.service.sendHeartRequest
import com.study.sddodyandroid.ui.component.study.EmptyComingSoon

@SuppressLint("UnrememberedMutableState", "MutableCollectionMutableState")
@Composable
fun PartOfFeedContent(navController: NavHostController,memberId : Long,
                            isStudyReview : Boolean = false,
                      studyId : Long? = null
                      ) {
    val rememberCommunityContent : MutableState<MutableList<BoardResponseDto>> = remember { mutableStateOf(mutableListOf()) }
    val context = LocalContext.current
    val rememberLoading =  remember{ mutableStateOf(false)}
    val sharedPreferences = context.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE)
    val myNickname = sharedPreferences.getString("nickname","")

    getMemberBoardList(memberId = memberId,context,rememberCommunityContent,rememberLoading,isStudyReview = isStudyReview,
        studyId = studyId)

    if(!rememberLoading.value){
        LoadingIndicator()
    } else if(rememberCommunityContent.value.size == 0){
        if(isStudyReview){
            EmptyComingSoon("작성한 후기가 없어요")
        }else{
            EmptyComingSoon("작성한 커뮤니티가 없어요")
        }
    } else{
        rememberCommunityContent.value.forEach{
            CommunityContentCard(it = it, navController =navController, context = context, myNickname = myNickname, noAdditionalInput = true)
        }

//        LazyColumn(){
//            items(rememberCommunityContent.value){
//                CommunityContentCard(it = it, navController =navController , context = context, myNickname = myNickname, noAdditionalInput = true)
//            }
//        }

    }
//    rememberCommunityContent = mutableStateOf(getLocalBoardResponse().toMutableList())

}
