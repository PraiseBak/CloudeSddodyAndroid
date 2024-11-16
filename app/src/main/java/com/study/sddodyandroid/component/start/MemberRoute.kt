package com.study.sddodyandroid.component.start

import MemberScreen
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.android.gms.location.FusedLocationProviderClient
import com.study.sddodyandroid.common.CheckedEnumViewModel
import com.study.sddodyandroid.dto.MemberInfoDto
import com.study.sddodyandroid.service.getMemberInfo

@Composable
fun MemberRoute(
    memberId : String? = "",
    isModifyUser : Boolean = false,
    type: String,
    navController: NavHostController,
    viewModel: CheckedEnumViewModel,
    onNavUp: () -> Unit,
    fusedLocationProviderClient: FusedLocationProviderClient
){
    val context = LocalContext.current
    val rememberMemberInfo : MutableState<MemberInfoDto?> = remember{ mutableStateOf(null) }
    var rememberIsLoading : MutableState<Boolean> = remember { mutableStateOf(false)}
    //유저 수정모드고 값을 안받았다면 getMemberInfo함
    if(isModifyUser && viewModel.languageList.value.size == 0){
        memberId?.let {
            getMemberInfo(context = context,rememberMemberInfo= rememberMemberInfo,memberId =it.toLong(),rememberIsLoading)
        }
    }

    if(rememberIsLoading.value && viewModel.languageList.value.size == 0){
        rememberMemberInfo.value?.let {
            viewModel.setMemberInfo(it)
        }
    }

    BackHandler {
        if (!viewModel.onBackPressed()) {
            onNavUp()
        }
    }

    if((isModifyUser && viewModel.languageList.value.size != 0) || !isModifyUser){
        println("member screen")
        MemberScreen(
            isModifyUser = isModifyUser,
            type = type,
            navController = navController,
            viewModel = viewModel,
            onClosePressed = { onNavUp() },
            onPreviousPressed = {  },
            onNextPressed = { /*TODO*/ },
            onDonePressed = { /*TODO*/ },
            fusedLocationProviderClient = fusedLocationProviderClient,
        )
     }else if(rememberIsLoading.value){
    }
}

