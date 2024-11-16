package com.study.sddodyandroid.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.MapsInitializer
import com.study.sddodyandroid.R
import com.study.sddodyandroid.common.CheckedEnumViewModel
import com.study.sddodyandroid.common.RetrofitInstance
import com.study.sddodyandroid.common.StudyAdminViewModel
import com.study.sddodyandroid.common.getUserProfileDtoByToken
import com.study.sddodyandroid.component.chatroom.ChatRoomListView
import com.study.sddodyandroid.component.chatroom.ChatRoomView
import com.study.sddodyandroid.component.chatroom.ImageZoom
import com.study.sddodyandroid.component.community.CommunityAddView
import com.study.sddodyandroid.component.community.CommunityListAppBar
import com.study.sddodyandroid.component.community.CommunityView
import com.study.sddodyandroid.component.main.MainAppBar
import com.study.sddodyandroid.component.main.MemberSetting
import com.study.sddodyandroid.component.main.createstudy.CreateStudyRoute
import com.study.sddodyandroid.component.main.createstudy.SelectedCreateView
import com.study.sddodyandroid.component.main.profile.ProfileView
import com.study.sddodyandroid.component.start.MainView
import com.study.sddodyandroid.component.study.StudyAdminView
import com.study.sddodyandroid.component.study.StudyAppBar
import com.study.sddodyandroid.component.study.StudyDetailView
import com.study.sddodyandroid.component.study.StudyListMap
import com.study.sddodyandroid.component.study.StudyView
import com.study.sddodyandroid.dto.StudyResponseDto
import com.study.sddodyandroid.helper.study.StudyOrderType
import com.study.sddodyandroid.service.ChatService
import com.study.sddodyandroid.service.initNotificationSubscribe
import com.study.sddodyandroid.service.modifyBoard
import com.study.sddodyandroid.service.sendBoardCreateRequest
import com.study.sddodyandroid.service.uriToFile
import com.study.sddodyandroid.ui.common.CommonBackgroundColor
import com.study.sddodyandroid.ui.component.main.SddodyBottomNavigationBar
import com.study.sddodyandroid.ui.component.study.SddodyTopAppBar
import com.study.sddodyandroid.ui.component.study.StudyDetailBottomBar
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientSecretBasic
import net.openid.appauth.ResponseTypeValues
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

/*
메인화면(시작하기 화면 아님)
 */
class MainActivity : ComponentActivity() {
    private val checkedEnumViewModel: CheckedEnumViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var service: AuthorizationService
    private val studyAdminViewModel : StudyAdminViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        validateCurToken(this);
        MapsInitializer.initialize(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        service = AuthorizationService(this)
        val sharedPreferences = this.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE)

        if(sharedPreferences.getString("accessToken",null ) == null){
            applicationContext.getSharedPreferences("KakaoLoginPreferences",Context.MODE_PRIVATE).edit()
                .clear()
                .apply()
            val intent = Intent(this, StartActivity::class.java)
            this.startActivity(intent)
        }

        val memberId = sharedPreferences.getString("memberId","")

        ChatService.initMemberSubscribe("member_${memberId.toString()}")
        initNotificationSubscribe("notification_${memberId.toString()}")
        println("구독 notification_ ${memberId.toString()}")

        val isDeletedUser = sharedPreferences.getBoolean("isDeleted",false)
        if(isDeletedUser){
            val intent = Intent(this, StartActivity::class.java)
            this.startActivity(intent)
        }

        setContent {
            SetMainNavHost(
                fusedLocationClient = fusedLocationClient,
                isMemberRedirect = intent.getBooleanExtra("isMemberRedirect",false),
                onGithubAuth = { githubAuth(this) },
                studyAdminViewModel = studyAdminViewModel,
            )
            //            StartNavHost(
//                context = LocalContext.current,
//                viewModel = checkedEnumViewModel,
//                fusedLocationProviderClient = fusedLocationClient
//            )
        }
    }

    private fun validateCurToken(context : Context) {
        val apiService = RetrofitInstance.apiService
        val call = apiService.isValidToken();
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(!response.isSuccessful){
                    Toast.makeText(context, "로그인 토큰이 만료되었습니다. 재로그인 해주세요", Toast.LENGTH_SHORT).show();
                    context.getSharedPreferences("KakaoLoginPreferences",Context.MODE_PRIVATE).edit()
                        .clear()
                        .apply()
                    val intent = Intent(context, StartActivity::class.java)
                    context.startActivity(intent)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
            }
        })
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK) {
            val ex = AuthorizationException.fromIntent(it.data!!)
            val result = AuthorizationResponse.fromIntent(it.data!!)

            if (ex != null){
                Log.e("Github Auth", "launcher: $ex")
            } else {
                val secret = ClientSecretBasic(this.resources.getString(R.string.GITHUB_CLIENT_SECRET))
                val tokenRequest = result?.createTokenExchangeRequest()

                service.performTokenRequest(tokenRequest!!, secret) {res, exception ->
                    if (exception != null){
                        Log.e("Github Auth", "launcher: ${exception.error}" )
                    } else {
                        val token = res?.accessToken
                        token?.let { it1 -> getUserProfileDtoByToken(it1,this.baseContext) }
                    }
                }
            }
        }
    }

    private fun githubAuth(context : Context) {
        val redirectUri = Uri.parse("sddody://github-auth/github/oauth/callback"    )
        val authorizeUri = Uri.parse("https://github.com/login/oauth/authorize")
        val tokenUri = Uri.parse("https://github.com/login/oauth/access_token")
        val GITHUB_CLIENT_ID = context.resources.getString(R.string.GITHUB_CLIENT_ID)

        val config = AuthorizationServiceConfiguration(authorizeUri, tokenUri)
        val request = AuthorizationRequest
            .Builder(config, GITHUB_CLIENT_ID, ResponseTypeValues.CODE, redirectUri)
            .setScopes("user repo admin")
            .build()

        val intent = service.getAuthorizationRequestIntent(request)
        launcher.launch(intent)
    }

}


object MainViewRoute {
    const val CREATE_PORTFOLIO = "portfolio/add"
    const val MAIN = "mainView"
    const val STUDY_LIST = "study/list"
    const val COMMUNITY_LIST = "community/list"
    const val PROFILE = "profile"
    const val CREATE_STUDY = "Create"
}




@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetMainNavHost(
    fusedLocationClient: FusedLocationProviderClient,
    isMemberRedirect: Boolean,
    onGithubAuth: () -> Unit,
    studyAdminViewModel: StudyAdminViewModel,
//        viewModel: CheckedEnumViewModel,
){
    var navController = rememberNavController()
    var imageUriList = mutableListOf<File>()
    var rememberImageUriList = remember{ mutableStateOf(imageUriList) }
    var context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uriList ->
            if(uriList.size > 10){
                Toast.makeText(context, "이미지 개수는 최대 10개입니다", Toast.LENGTH_SHORT).show()
                return@rememberLauncherForActivityResult
            }
            val maxSizeBytes = 10 * 1024 * 1024 // 10MB 제한

            // 이미지 파일의 크기 확인
            for (uri in uriList) {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val curSize = inputStream.available().toLong()

                    // 이미지 크기가 제한을 초과하면 사용자에게 알림
                    if (curSize > maxSizeBytes.toLong()) {
                        Toast.makeText(context, "$curSize $maxSizeBytes 이미지 크기가 너무 커요.", Toast.LENGTH_SHORT).show()
                        return@rememberLauncherForActivityResult
                    }
                }
            }

            val fileList : MutableList<File> = mutableListOf()
            // 이미지 크기가 제한을 초과하지 않으면 리스트 업데이트
            uriList.forEach{
                uriToFile(context,it)?.let { file -> fileList.add(file)}
            }
            rememberImageUriList.value = fileList

        }
    )

    var startDestination = "mainView"
//    startDestination = "${MainViewRoute.Profile}/0"
//    val startDestination = "mainView"
    if(isMemberRedirect){
        startDestination = "${MainViewRoute.PROFILE}/0"
    }
//    val startDestination = "study/map"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("mainView"){
            MainScaffold(navController,true, topBar = {
                MainAppBar("Sddody",isFullScreen = false,modifier = Modifier,
                    navController = navController
                )
            }) {
                Box(modifier = Modifier
//                    .background(CommonBackgroundColor().copy(0.5f))
                    .background(Color.White)
                    .fillMaxSize()){
                    Box(modifier = Modifier.padding(vertical = 10.dp))
                    {
                         MainView(navController)
                    }
                }
            }
        }

        composable("chatRoomList"){
            CommunityView(navController)
        }

        composable("community/list"){
            MainScaffold(navController,topBar = {
                CommunityListAppBar("커뮤니티",isFullScreen = false,modifier = Modifier,
                    navController = navController
                )})
            {
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)){

                    item{
                        CommunityView(navController)
                    }
                }
            }

        }

        composable("community/add"){
            rememberImageUriList.value = mutableListOf()
            MainTopBarScaffold(
                topBar = {SddodyTopAppBar(
                    "게시글 작성",
                    true,
                    Modifier,
                    navController,
                    studyAdminViewModel
                )}
            ) {
                CommunityAddView(
                    navController, rememberImageUriList,Modifier.padding(it),
                    onWriteSubmit = { boardDto ->
                        sendBoardCreateRequest(
                            boardDto,
                            rememberImageUriList,
                            context,
                            navController
                        )
                    },
                    onImageUpload = { galleryLauncher.launch("image/*")},
                )
            }
        }

        //스터디에서 게시글 작성할 때
        composable("community/add/{id}"){backStackEntry ->
            val studyId = backStackEntry.arguments?.getString("id")?.toLong()
            studyId?.let{
                studyId ->

                rememberImageUriList.value = mutableListOf()
                MainTopBarScaffold(
                    topBar = {SddodyTopAppBar(
                        "스터디 후기",
                        true,
                        Modifier,
                        navController,
                        studyAdminViewModel,
                    )}
                ) {
                    CommunityAddView(
                        navController, rememberImageUriList,Modifier.padding(it),
                        onWriteSubmit = { boardDto ->
                            sendBoardCreateRequest(
                                boardDto,
                                rememberImageUriList,
                                context,
                                navController
                            )
                        },
                        onImageUpload = { galleryLauncher.launch("image/*") },
                        studyId = studyId
                    )
                }
            }

        }

        composable("community/modify/{id}"){ backStackEntry ->
            val boardId = backStackEntry.arguments?.getString("id")?.toLong()
            boardId?.let {id->
                MainTopBarScaffold(
                    topBar = {SddodyTopAppBar(
                        "게시글 수정",
                        true,
                        Modifier,
                        navController,
                        studyAdminViewModel
                    )}
                ) {
                    CommunityAddView(
                        navController, rememberImageUriList,Modifier.padding(it),
                        onWriteSubmit = { boardDto ->
                            modifyBoard(
                                id,
                                boardDto,
                                context,
                                navController,
                                rememberImageUriList
                            )
                        },
                        onImageUpload = { galleryLauncher.launch("image/*") },
                        modifyBoardId = id
                    )
                }
            }
        }


        //유저 수정 페이지
        composable("user/setting"){
            MainTopBarScaffold(
                topBar = {SddodyTopAppBar(
                    "유저 설정",
                    true,
                    Modifier,
                    navController,
                    studyAdminViewModel
                )}
            ) {

                Box(modifier = Modifier
                    .padding(it)
                    .background(
                        CommonBackgroundColor()
                    )){
                    MemberSetting(navController)
                }
            }
        }

        composable("chatRoom/{id}") { backStackEntry ->
            val studyId = backStackEntry.arguments?.getString("id")
            if (studyId != null) {
                rememberNavController()

                rememberImageUriList.value = mutableListOf()
                MainTopBarScaffold(
                    topBar = {SddodyTopAppBar(
                        "채팅룸",
                        true,
                        Modifier,
                        navController,
                        studyAdminViewModel,
                    )}
                ) {
                    ChatRoomView(navController, studyId.toLong(), rememberImageUriList, Modifier.padding(it)) {
                        galleryLauncher.launch("image/*")
                    }
                }
            }
        }

        composable("image/zoom/{imageUri}") { backStackEntry ->
            val imageUri = backStackEntry.arguments?.getString("imageUri")
            MainTopBarScaffold(
                topBar = {SddodyTopAppBar(
                    "",
                    true,
                    Modifier,
                    navController,
                    studyAdminViewModel
                )}
            ) {
                ImageZoom(imageUri, backStackEntry,Modifier.padding(it))
            }
        }

        composable("${MainViewRoute.PROFILE}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            ProfileView(navController = navController, onGithubAuth, id)

        }

        composable(MainViewRoute.STUDY_LIST) {
            var orderType = remember{mutableStateOf(StudyOrderType.RECENT_CREATE)}

            MainScaffold(navController,true, topBar = {
                StudyAppBar("스터디 리스트",isFullScreen = true,modifier = Modifier,
                    navController = navController, onSortClick = {
                        orderType.value = it
                    })
            } , content = {
                StudyView(navController = navController, orderType = orderType)
            })
        }


        /*
        포트폴리오
         */
        composable(MainViewRoute.CREATE_PORTFOLIO) {
            rememberImageUriList.value = mutableListOf()

            Column {
                SddodyTopAppBar(
                    "포트폴리오 작성",
                    true,
                    Modifier,
                    navController,
                    studyAdminViewModel
                )
                Box(
                    modifier = Modifier
                        .background(color = CommonBackgroundColor())
                        .fillMaxSize()
                ){

                    CommunityAddView(
                        navController, rememberImageUriList,
                        onWriteSubmit = { boardDto ->
                            sendBoardCreateRequest(
                                boardDto,
                                rememberImageUriList,
                                context,
                                navController
                            )
                        },
                        isPortfolio = true,

                        onImageUpload = { galleryLauncher.launch("image/*") },
                    )
                }
            }

        }

        composable(MainViewRoute.CREATE_STUDY) {
            CreateStudyRoute(
                onCreateComplete = {
                    navController.navigate(MainViewRoute.STUDY_LIST)
                },
                onNavUp = navController::navigateUp,
                fusedLocationClient = fusedLocationClient,
                navController = navController
            )
        }



        composable("study/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLong()
            var rememberStudy : MutableState<StudyResponseDto?> = remember{mutableStateOf(null)}
            if (id != null) {

                MainScaffold(
                    navController, true,
                    topBar = {
                        SddodyTopAppBar(
                            rememberStudy.value?.title ?: "",
                            true,
                            modifier =Modifier,
                            navController,
                            studyAdminViewModel
                        )
                    },
                    bottomBar = {
                        rememberStudy.value?.let {
                            StudyDetailBottomBar(
                                modifier = Modifier,
                                study = it,
                                navController = navController,
                                context,
                                id
                            )
                        }
                    },
                ){
                    StudyDetailView(id,navController, rememberStudy = rememberStudy)
                }
            }
        }


        composable("study/{id}/admin"){
                backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLong()

            if (id != null) {
                StudyAdminView(navController,id,studyAdminViewModel)
            }
        }

        composable("study/community/${id}"){
                backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toLong()
            if (id != null) {
                MainTopBarScaffold(
                    topBar = {SddodyTopAppBar(
                        text = "스터디 게시판",
                        isFullScreen = true,
                        navController = navController,
                        studyAdminViewModel = studyAdminViewModel,
                    ) }
                ){
                    CommunityView(
                        navController = navController,
                        isPreview = true,
                        onChangeCommunityViewClick = {navController.navigate("community/list")},
                        modifier = Modifier.padding(it),
                        studyId = id
                    )
                }

            }
        }
        /**
         * 진행되고 있는 스터디를 지도에 표시
         */
        composable("study/map"){
            MainTopBarScaffold(
                topBar = {SddodyTopAppBar(
                    "진행되고 있는 스터디 ",
                    true,
                    Modifier,
                    navController,
                    studyAdminViewModel,
                )}
            ) {
                StudyListMap(navController,fusedLocationClient,Modifier.padding(it))
            }

        }

        composable("selectedCreate"){
            MainTopBarScaffold(
                topBar = {SddodyTopAppBar(
                    "개설하기",
                    true,
                    Modifier,
                    navController,
                    studyAdminViewModel
                )}
            ) {
                SelectedCreateView(navController = navController, modifier = Modifier.padding(it))
            }
            //
        }

        /*
        챗 리스트
         */
        composable("chatRoom/list"){
            MainTopBarScaffold(topBar = {SddodyTopAppBar(
                "채팅",
                true,
                Modifier,
                navController,
                studyAdminViewModel
            )}) {
                Column(modifier= Modifier.padding(it)){
                    ChatRoomListView(navController = navController)
                }

            }
        }


    }
}


@Composable
fun MainScaffold(
    navController: NavHostController,
    isProfile: Boolean = false,
    topBar: @Composable() (() -> Unit)? = null,
    bottomBar: @Composable() (() -> Unit)? = null,
    content: @Composable () -> Unit,

){
    Scaffold(
        bottomBar = {if(bottomBar == null) SddodyBottomNavigationBar(navController, Modifier) else bottomBar()},
        topBar = {
            if (topBar != null) {
                topBar()
            }
        }
    ) {innerpadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .background(color = CommonBackgroundColor())
            .padding(innerpadding)){
            content()
            val fabModifier = Modifier
                .align(Alignment.BottomEnd)
            val modifier = Modifier.align(Alignment.BottomEnd)
            //바텀내비게이션바
            if(!isProfile){

            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainTopBarScaffold(topBar: @Composable () -> Unit,content: @Composable (PaddingValues) -> Unit){
    Scaffold(
        topBar = topBar
    ) {innerPadding ->
        content(innerPadding)
    }
}






