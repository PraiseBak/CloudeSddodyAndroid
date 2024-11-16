package com.study.sddodyandroid.component.portfolio

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.component.chatroom.LoadingIndicator
import com.study.sddodyandroid.component.community.CommunityContentCard
import com.study.sddodyandroid.component.community.CommunityContentCardList
import com.study.sddodyandroid.dto.BoardResponseDto
import com.study.sddodyandroid.dto.PageDto
import com.study.sddodyandroid.service.getBoardList
import com.study.sddodyandroid.service.getMemberBoardList
import com.study.sddodyandroid.ui.component.study.EmptyComingSoon

@Composable
fun PortfolioView(navController: NavHostController, memberId: Long) {
    val rememberCommunityContent : MutableState<MutableList<BoardResponseDto>> = remember { mutableStateOf(mutableListOf()) }
    val context = LocalContext.current
    val rememberLoading =  remember{ mutableStateOf(false) }
    val sharedPreferences = context.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE)
    val myNickname = sharedPreferences.getString("nickname","")

    getMemberBoardList(memberId = memberId,context,rememberCommunityContent,rememberLoading,isPortfolio = true)

    if(!rememberLoading.value){
        LoadingIndicator()
    } else if(rememberCommunityContent.value.size == 0){
        EmptyComingSoon("작성한 포트폴리오가 없어요")
    } else{
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.systemBars))

        rememberCommunityContent.value.forEach{
            CommunityContentCard(it = it, navController =navController , context = context, myNickname = myNickname, noAdditionalInput = true)
        }
//        LazyColumn(){
//            items(rememberCommunityContent.value){
//                CommunityContentCard(it = it, navController =navController , context = context, myNickname = myNickname, noAdditionalInput = true)
//            }
//        }
        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))

    }
}