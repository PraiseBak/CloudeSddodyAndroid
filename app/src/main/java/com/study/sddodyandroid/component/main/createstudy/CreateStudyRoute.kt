package com.study.sddodyandroid.component.main.createstudy

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.gms.location.FusedLocationProviderClient
import com.study.sddodyandroid.common.CreateStudyViewModel
import com.study.sddodyandroid.common.CreateStudyViewModelFactory
import com.study.sddodyandroid.common.CreateStudyEnum
import com.study.sddodyandroid.dto.MemberInfoDto
import com.study.sddodyandroid.helper.DeveloperEnum
import com.study.sddodyandroid.helper.FrameworkEnum
import com.study.sddodyandroid.helper.PhotoUriManager
import com.study.sddodyandroid.service.setMyMemberInfo


private const val CONTENT_ANIMATION_DURATION = 300
@Composable
fun CreateStudyRoute(
    onCreateComplete: () -> Unit,
    onNavUp: () -> Unit,
    fusedLocationClient: FusedLocationProviderClient,
    navController : NavHostController
){
    val viewModel: CreateStudyViewModel = viewModel(
        factory = CreateStudyViewModelFactory(PhotoUriManager(LocalContext.current))
    )
    val context = LocalContext.current

    val surveyScreenData = viewModel.createStudyScreenData ?: return

    val memberInfo : MutableState<MemberInfoDto?> = remember{ mutableStateOf(null) }

    var rememberPressNext by mutableStateOf(false)

    if(viewModel.title.value.isEmpty()){
        setMyMemberInfo(context,viewModel)
    }

    BackHandler {
        if (!viewModel.onBackPressed()) {
            onNavUp()
        }
    }

    CreateStudyScaffold(
        createStudyScreenData = surveyScreenData,
        isNextEnabled = viewModel.isNextEnabled,
        onClosePressed = {
            onNavUp()
        },
        onPreviousPressed = { viewModel.onPreviousPressed() },
        onNextPressed = { viewModel.onNextPressed() },
        onDonePressed = {
            if(!rememberPressNext){
                rememberPressNext = true
                viewModel.onDonePressed(onCreateComplete,context, navController = navController)
            }
        }
    ){ paddingValues ->

        val modifier = Modifier
            .padding(paddingValues)

        AnimatedContent(
            targetState = surveyScreenData,
            transitionSpec = {
                val animationSpec: TweenSpec<IntOffset> = tween(CONTENT_ANIMATION_DURATION)

                val direction = getTransitionDirection(
                    initialIndex = initialState.questionIndex,
                    targetIndex = targetState.questionIndex,
                )

                slideIntoContainer(
                    towards = direction,
                    animationSpec = animationSpec,
                ) togetherWith slideOutOfContainer(
                    towards = direction,
                    animationSpec = animationSpec
                )
            },
            label = "surveyScreenDataAnimation"
        ){targetState ->
            when (targetState.createStudyEnum) {
                //title
                CreateStudyEnum.TITLE_CONTENT -> {
                    SetStudyTitleAndContentView(
                        onOptionSelected = viewModel::onTitleResponse,
                        viewModel = viewModel,
                        modifier = modifier,
                    )
                }
                // 배경사진
                CreateStudyEnum.IMAGE -> {
                    SetStudyImageView(
                        imageUri = viewModel.imageUriList,
                        viewModel = viewModel,
                        getNewImageUri = viewModel::getNewSelfieUri,
                        onPhotoTaken = viewModel::onImgResponse,
                        modifier = modifier,
                    )
                }

                //위치지정
                CreateStudyEnum.LOCATION -> {
                    SetActiveLocation(
                        location = viewModel.location,
                        viewModel = viewModel,
                        fusedLocationClient = fusedLocationClient,
                        modifier = modifier
                    )
                }

                //가입승인 or 허용
                CreateStudyEnum.ALLOW_STUDY -> {
                    SetAllowStudyRoom(
                        allowStudy = viewModel.isOpenToEveryone2,
                        viewModel = viewModel,
                        onOptionSelected = viewModel::onIsOpenToEveryoneResponse,
                        modifier = modifier,
                    )
                }
                //필요 직종
                CreateStudyEnum.REQUIRE_DEVELOPER ->{
                    SetRequireDeveloper(
                        developerEnumList = DeveloperEnum.entries,
                        viewModel = viewModel,
                        onDevSelected = viewModel::onRequireDeveloperResponse,
                        modifier = modifier
                    )

//                    SetMaxMember(
//                        value = viewModel.maxMember,
//                        onValueChange = viewModel::onMaxMember,
//                        viewModel = viewModel,
//                        modifier = modifier
//                    )
                }
                // 필요 테크
                CreateStudyEnum.REQUIRE_STACK_TECH -> {
                    SetRequireDevFramework(
                        frameworkEnumList = FrameworkEnum.values().toList(),
                        viewModel = viewModel,
                        onFrameworkSelected = viewModel::onRequireStackTech,
                        modifier = modifier
                    )
                }

//                맥스 멤버
                CreateStudyEnum.MAX_MEMBER -> {
                    SetMaxMember(
                        value = viewModel.maxMember,
                        onValueChange = viewModel::onMaxMember,
                        viewModel = viewModel,
                        modifier = modifier
                    )
                }
            }
        }
    }
}

private fun getTransitionDirection(
    initialIndex: Int,
    targetIndex: Int
): AnimatedContentTransitionScope.SlideDirection {
    return if (targetIndex > initialIndex) {
        // Going forwards in the survey: Set the initial offset to start
        // at the size of the content so it slides in from right to left, and
        // slides out from the left of the screen to -fullWidth
        AnimatedContentTransitionScope.SlideDirection.Left
    } else {
        // Going back to the previous question in the set, we do the same
        // transition as above, but with different offsets - the inverse of
        // above, negative fullWidth to enter, and fullWidth to exit.
        AnimatedContentTransitionScope.SlideDirection.Right
    }
}