package com.study.sddodyandroid.component.main.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.GroupRemove
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.study.sddodyandroid.R
import com.study.sddodyandroid.activity.MainScaffold
import com.study.sddodyandroid.common.getGithubUserProfileByName
import com.study.sddodyandroid.component.conversation.AsyncImageWithHeader
import com.study.sddodyandroid.component.image.GithubGrassImage
import com.study.sddodyandroid.component.image.UnknownMemberImage
import com.study.sddodyandroid.component.portfolio.PortfolioView
import com.study.sddodyandroid.dto.GithubUserProfileDto
import com.study.sddodyandroid.dto.MemberInfoDto
import com.study.sddodyandroid.helper.DeveloperEnum
import com.study.sddodyandroid.helper.FrameworkEnum
import com.study.sddodyandroid.helper.LanguageEnum
import com.study.sddodyandroid.helper.member.MemberStatus
import com.study.sddodyandroid.service.getMemberInfo
import com.study.sddodyandroid.ui.common.CommonButtonColor
import com.study.sddodyandroid.ui.common.CommonSecondaryBackgroundColor
import com.study.sddodyandroid.ui.component.main.profile.PartOfFeedContent
import com.study.sddodyandroid.ui.component.main.profile.PartOfStudyContent
import com.study.sddodyandroid.ui.component.main.profile.ProfileAppBar
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ExperimentalToolbarApi
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnrememberedMutableState")
@Composable
fun ProfileView(
    navController: NavHostController,
    onGithubAuth: () -> Unit,
    id: String?,
){
    val memberId = id?.toLong() ?: 0L
    val rememberMemberInfo : MutableState<MemberInfoDto?> = remember{ mutableStateOf(null) }
    getMemberInfo(LocalContext.current,rememberMemberInfo,memberId)

    var nickname = ""
    val memberInfo = rememberMemberInfo.value

    memberInfo?.let{
        nickname = it.nickname
    }

    MainScaffold(navController, true, topBar = {
        ProfileAppBar(
            nickname,
            isFullScreen = false,
            modifier = Modifier,
            navController = navController
        )
    }) {
        memberInfo?.let {
            if(memberInfo.memberStatus == MemberStatus.ROLE_DELETED && memberId != 0L){
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "비활성화 된 계정입니다", fontSize = 24.sp)
                }
            }else{
                ProfileContentCard(memberId = memberId, navController, memberInfo, onGithubAuth)
            }
        }
    }
}



@Composable
fun CollapsingToolbarScaffoldExample() {
    val scaffoldState = rememberCollapsingToolbarScaffoldState()
    val toolbarHeight = 200.dp
    val scrollState = rememberScrollState()

    CollapsingToolbarScaffold(
        modifier = Modifier.fillMaxSize(),
        toolbar = {
            Toolbar(title = "Collapsing Toolbar")
        },
        body = {
            // Scrollable content
            // Here you can place your scrollable content, such as LazyColumn
            // In this example, a simple scrollable column is used
        },
        state = scaffoldState,
        scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed, // 예시로 사용된 스크롤 전략

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Toolbar(title: String) {
    TopAppBar(
        title = { Text(text = title) },
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalToolbarApi::class, ExperimentalPagerApi::class)
@Composable
fun ProfileContentCard(
    memberId: Long,
    navController: NavHostController,
    memberDto: MemberInfoDto,
    onGithubAuth: () -> Unit,
    ) {
    val state = rememberCollapsingToolbarScaffoldState()
    val enabled by remember { mutableStateOf(true) }
    val onClick by remember { mutableStateOf(true) }

    LazyColumn(){
        item {
            Card(
                colors = CardDefaults.cardColors(
                    Color.White
                ),

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),

                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 0.dp, vertical = 8.dp)


            ) {
                val modifier = Modifier.align(Alignment.CenterHorizontally)

                ProfileBox(memberDto, modifier,onGithubAuth)

                ProfileDevRow(
                    modifier,
                    devLanguageList = memberDto.devLanguageEnumList,
                    devDeveloperList = memberDto.developerEnumList,
                    devFrameworkList = memberDto.frameworkEnumList
                )

            }
        }

        item {

            Box(modifier = Modifier.padding(top = 12.dp)

                ){
                val pages = listOf("스터디", "작성글","프로젝트","후기")
                val pagerState = rememberPagerState()
                var pageIndex by remember { mutableIntStateOf(0) }
                Column(modifier = Modifier
                    .fillMaxSize()
                    .background(color = CommonSecondaryBackgroundColor())
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 0.dp)
                            .fillMaxWidth()
                            .background(color = CommonSecondaryBackgroundColor()),
                    ) {
                        pages.forEachIndexed { index, title ->
                            Button(
                                colors = CommonButtonColor(),
                                onClick = { pageIndex = index },

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),

                                shape = when (index) {
                                    0 -> RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp) // 첫 번째 버튼의 왼쪽 둥근 모서리
                                    pages.size - 1 -> RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp) // 마지막 버튼의 오른쪽 둥근 모서리
                                    else -> RectangleShape // 나머지 버튼은 모서리를 적용하지 않음
                                }
                            ) {
                                if(index == 5){
                                    Icon(Icons.Default.MenuBook,contentDescription = null)
                                }else if(index == 4) {
                                    Icon(Icons.Default.WorkHistory,contentDescription = null)
                                } else{
                                    Text(text = title,style = MaterialTheme.typography.bodyMedium,
                                        overflow = TextOverflow.Clip)
                                }
                            }
                        }
                    }
                    Column(modifier = Modifier.padding(horizontal = 10.dp)){
                        // 페이지 내용
                        if (pageIndex == 0) {
                            PartOfStudyContent(navController = navController, memberId)
                        }
                        if (pageIndex == 1) {
                            PartOfFeedContent(navController = navController,memberId)
                        }
                        if (pageIndex == 2) {
                            PortfolioView(navController = navController,memberId)
                        }
                        if (pageIndex == 3) {
                            PartOfFeedContent(navController = navController,memberId,isStudyReview = true)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileExtraInfo(memberDto: MemberInfoDto) {
    Row(
    ){
        Icon(
            imageVector = Icons.Default.Work,
            contentDescription = "직종",
            modifier = Modifier
                .size(24.dp)
                .padding(start = 10.dp)
        )
        Text(
            text = memberDto.devLevelEnum.text,
            style = MaterialTheme.typography.labelSmall,

            modifier = Modifier
                .padding(start = 4.dp, top = 6.dp)
        )
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = "개발 공부 기간",
            modifier = Modifier
                .size(24.dp)
                .padding(start = 10.dp)
        )

        Text(
            text = memberDto.devYearEnum.yearStr,
            style = MaterialTheme.typography.labelSmall,

            modifier = Modifier
                .padding(start = 4.dp, top = 6.dp)
        )


        if(memberDto.memberStatus == MemberStatus.ROLE_DELETED){
            Icon(
                imageVector = Icons.Default.GroupRemove,
                contentDescription = "휴면상태",
                modifier = Modifier
                    .size(24.dp)
                    .padding(start = 10.dp)
            )
            Text(text = "현재 휴면상태입니다",modifier = Modifier.padding(start = 8.dp))
        }


    }



}


@Composable
fun GithubConnectButton(
    onClick: () -> Unit,
    iconSize: Dp = Dp.Unspecified,
    iconTint: Color? = null
) {
    val painter = painterResource(id = R.drawable.github)

    Row(
        modifier = Modifier
            .size(iconSize)
            .padding(20.dp)
            .fillMaxWidth(),
    ) {
        Icon(
            painter = painter,
            contentDescription = "GitHub",
            tint = iconTint ?: Color.Unspecified,
            modifier = Modifier
                .size(20.dp)
                .clickable { onClick() }
        )

        Text("아직 깃허브와 연동하지 않았어요",
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}


@Composable
fun GithubUserCard(memberDto: MemberInfoDto) {
    val githubUri = "https://github.com/${memberDto.githubNickname}"
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { }
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUri))
    var rememberGithubUserProfile :MutableState<GithubUserProfileDto?> = remember{ mutableStateOf(null) }

    if(rememberGithubUserProfile.value == null){
        getGithubUserProfileByName(LocalContext.current,memberDto.githubNickname!!,rememberGithubUserProfile)
    }else{
        rememberGithubUserProfile.value?.let{
            if(it.nickname != null){
                Column(modifier= Modifier.fillMaxWidth()) {
                    val painter = painterResource(id = R.drawable.github)
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ){
                            Row(modifier = Modifier.padding(top = 10.dp)) {
                                Image(painter = painter, contentDescription = "깃허브",modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        launcher.launch(intent)
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(it.nickname)
                            }
                        }

                        if(it.publicRepos != -1){
                            Column(horizontalAlignment = Alignment.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 10.dp)

                            ) {
                                Text("레포지토리 수 ${it.publicRepos} ")
                                Text("팔로워 수 ${it.followers} ")
                            }
                        }

                        Box(modifier = Modifier.padding(10.dp)){
                            GithubGrassImage(it.nickname)
                        }

                    }


                }
            }

        }


    }




}

@Composable
fun IntroduceDynamicBox(text: String, maxLines: Int = 3) {
    var expanded by remember { mutableStateOf(false) }
    Box (
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(start = 5.dp)
    ){
        Text(
            text = if (expanded || text.lines().size <= maxLines) text else text.lines().take(maxLines).joinToString("\n"),
            maxLines = if (expanded) Int.MAX_VALUE else maxLines,
            modifier = Modifier
        )
        if (!expanded && text.lines().size > maxLines) {
            // 더 보기 버튼 또는 아이콘 추가
            Text(
                text = "더 보기",
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clickable { expanded = true }
            )
        }
        if (expanded) {
            // 더 보기 버튼 또는 아이콘 추가
            Text(
                text = "접기",
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clickable { expanded = false }
            )
        }
    }
}

@SuppressLint("UseCompatLoadingForDrawables")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileBox(
    memberDto: MemberInfoDto,
    modifier: Modifier,
    onGithubAuth: () -> Unit
){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
    ){
        Row() {
            Column(modifier = Modifier
                .weight(1f)
                .padding(top = 12.dp)
//                .background(Color.LightGray, RoundedCornerShape(10.dp))

            ) {
                if(memberDto.memberProfileImgSrc.isNotEmpty()){
                    AsyncImageWithHeader(uri = memberDto.memberProfileImgSrc,isProfile = true)
                }else{
                    UnknownMemberImage(modifier = Modifier.size(60.dp))
                }
            }

            Box(modifier = Modifier.weight(4f)){
                Card(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, end = 10.dp)
                        .heightIn(min = 50.dp)
                ){
                    //추가 정보 직종 등
                    Text(modifier = Modifier.padding(vertical = 8.dp ,horizontal = 20.dp,),text = "자기소개",
                        style = MaterialTheme.typography.titleSmall)
                    Text(modifier = Modifier.padding(16.dp),text = "${memberDto.selfIntroduce}")
                    Spacer(modifier = Modifier.weight(4f))
                }
            }
        }

        Column(modifier = Modifier
            .align(Alignment.End)
            .padding(end = 10.dp)) {
            //추가 정보 직종 등
            ProfileExtraInfo(memberDto)
        }

        Column {
            if (memberDto.githubNickname == null) {
                GithubConnectButton(
                    onClick = {onGithubAuth()}
                )
            } else {
                GithubUserCard(memberDto)
            }
        }

//        OutlinedTextField(
//            value = memberDto.selfIntroduce,
//            onValueChange = { },
//            modifier = Modifier.fillMaxWidth(),
//            enabled = false, // 수정 불가능하도록 설정
//            colors = TextFieldDefaults.textFieldColors( // 글자색
//
//                cursorColor = Color.Black, // 커서 색상
//                disabledTextColor = Color.Black, // 비활성화된 텍스트 색상
//                containerColor = Color.White,
//            )
//        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileDevRow(
    modifier: Modifier,
    devLanguageList: List<LanguageEnum>,
    devDeveloperList: List<DeveloperEnum>,
    devFrameworkList: List<FrameworkEnum>
){
    val list = listOf(devLanguageList,devDeveloperList,devFrameworkList)
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ){
        Text(modifier = Modifier.padding(start = 12.dp,top=10.dp),text = "관심분야",
        style = MaterialTheme.typography.titleSmall)

        Column (
            modifier = Modifier.padding(0.dp)
        ){

            FlowRow() {
                list.forEach { list ->
                    list.forEach { it ->
                        Button(
                            colors = CommonButtonColor(),
                            onClick = {},
                            modifier = Modifier.padding(start = 10.dp,bottom= 10.dp,top = 2.dp)
                        ) {
                            Text(text = it.info)
                        }

                    }
                }
            }
        }

    }
}


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class, ExperimentalMaterialApi::class)
@Composable
fun ProfileViewPager(
    navController: NavHostController,
    memberId: Long,
){
    val pages = listOf("스터디", "커뮤니티","포트폴리오")
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    // LazyColumn의 스크롤 위치를 저장할 상태
    val scrollState = rememberLazyListState()

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                height = 2.dp,
                color = Color.LightGray
            )
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        // 각 탭을 추가
        pages.forEachIndexed { index, title ->
            Tab(
                text = { Text(text = title) },
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.scrollToPage(index)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

//    HorizontalPager(
//        modifier = Modifier
//            .fillMaxWidth(1f)
//            .fillMaxHeight(),
//        count = pages.size,
//        state = pagerState
//    ) {
//
//        Box(modifier = Modifier
//            .background(color = Color.LightGray.copy(alpha = 0.5f))) {
//            // 페이지 내용
//            if (it == 0) {
//                PartOfStudyContent(navController = navController, memberId)
//            }
//            if (it == 1) {
//                PartOfFeedContent(navController = navController,memberId)
//            }
//            if (it == 2) {
//                PortfolioView(navController = navController,memberId)
//            }
//        }
//    }


}




