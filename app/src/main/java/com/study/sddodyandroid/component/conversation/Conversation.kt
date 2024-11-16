@file:OptIn(ExperimentalMaterial3Api::class)

package com.study.sddodyandroid.component.conversation

/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.DrawerValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.NavigateBefore
import androidx.compose.material.icons.automirrored.outlined.NavigateNext
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.compose.jetchat.components.JetchatAppBar
import com.example.compose.jetchat.conversation.SymbolAnnotationType
import com.example.compose.jetchat.conversation.messageFormatter
import com.example.compose.jetchat.theme.JetchatTheme
import com.study.sddodyandroid.R
import com.study.sddodyandroid.common.ChatRoomViewModel
import com.study.sddodyandroid.component.image.UnknownMemberImage
import com.study.sddodyandroid.component.study.px2dp1
import com.study.sddodyandroid.dto.ChatDto
import com.study.sddodyandroid.dto.ChatInfoDto
import com.study.sddodyandroid.dto.MemberInfoDto
import com.study.sddodyandroid.service.ChatService
import com.study.sddodyandroid.service.compareAndFormatDates
import com.study.sddodyandroid.service.isShowTimeStamp
import com.study.sddodyandroid.ui.component.ShowMemberDrawer
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.min


/**
 * Entry point for a conversation screen.
 *
 * @param uiState [ConversationUiState] that contains messages to display
 * @param navigateToProfile User action when navigation to a profile is requested
 * @param modifier [Modifier] to apply to this layout node
 * @param onNavIconPressed Sends an event up when the user clicks on the menu
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationContent(
    uiState: ConversationUiState,
    navigateToProfile: (String) -> Unit,
    modifier: Modifier = Modifier,
    onNavIconPressed: () -> Unit = { },
    onImageUpload: () -> Unit = {},
    uploadImageUriList: MutableState<MutableList<File>>,
    chatRoomId: Long,
    authorMe: String?,
    viewModel: ChatRoomViewModel,
    navController: NavHostController,
    chatDtoList: MutableState<List<ChatInfoDto>?>,

    onImageClick: (String) -> Unit
) {
    val scrollState = rememberLazyListState()
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var searchQuery by remember {mutableStateOf("")}
    val memberList = viewModel.chatRoomDto.value?.chatRoomMemberInfoDtoList



    Scaffold(
        topBar = {
            ChannelNameBar(
                channelName = uiState.channelName,
                channelMembers = uiState.channelMembers,
                onNavIconPressed = onNavIconPressed,
                scrollBehavior = scrollBehavior,
                onSearchQueryChange = { it->
                    searchQuery = it
                },
                memberList = memberList
            )
        },
        // Exclude ime and navigation bar padding so this can be added by the UserInput composable
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime),
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
            Messages(
                isChatOver = uiState.isChatRoomOver,
                messages = uiState.messages,
                navigateToProfile = navigateToProfile,
                modifier = Modifier.weight(1f),
                scrollState = scrollState,
                authorMe = authorMe,
                viewModel = viewModel,
                searchQuery = searchQuery,
                onImageClick = onImageClick,
                navController = navController
            )

            LazySelectedImage(uploadImageUriList, Color.Unspecified)

            var textState = rememberSaveable(stateSaver = TextFieldValue.Saver) {
                mutableStateOf(TextFieldValue())
            }

            UserInput(
                isEnd = uiState.isChatRoomOver,
                onMessageSent = { content ->
//                    ChatDto()
//                    uiState.addMessage(
//                        Message(authorMe, content, timeNow)
//                    )
                },
                resetScroll = {
                    scope.launch {
                        scrollState.scrollToItem(0)
                    }
                },
                onImageUpload = { onImageUpload() },
                // let this element handle the padding so that the elevation is shown behind the
                // navigation bar
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding(),
                onSend = { content ->
                    var chatDto: ChatDto? = null
                    if(content.isNotBlank()){
                        chatDto = ChatDto(msg = content,chatRoomId=chatRoomId)
                    }

                    ChatService.sendMsg(chatDto,chatRoomId,
                        context = context,
                        sendImageList = uploadImageUriList,textState)
                },
                imageList = uploadImageUriList,
                textState = textState
            )
        }
    }
}

@Composable
fun LazySelectedImage(uploadImageUriList: MutableState<MutableList<File>>, color: Color?) {
    Box(modifier = Modifier.padding(top = 12.dp))  {
        if (uploadImageUriList.value.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp))
                    .background(color ?: Color.DarkGray)
                    .padding(12.dp)
            ) {
                items(uploadImageUriList.value) { imageUri ->
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                            .padding(4.dp)
                            .clip(shape = RoundedCornerShape(15.dp))
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center)
                        )

                        IconButton(
                            onClick = {
                                uploadImageUriList.value =
                                    uploadImageUriList.value.filter { it.path != imageUri.path }.toMutableList()
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(end = (60).dp, bottom = (50).dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "Close",
                                tint = Color.LightGray
                            )
                        }
                    }
                }
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelNameBar(
    channelName: String,
    channelMembers: Int,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onNavIconPressed: () -> Unit = { },
    onSearchQueryChange: (String) -> Unit,
    memberList: List<MemberInfoDto>?,
) {
    var searchInputArea by remember { mutableStateOf(false) }
    var searchFieldText by remember { mutableStateOf(TextFieldValue("")) }
    var drawerOpen = remember{ mutableStateOf(false) }
    var drawerState = rememberDrawerState(DrawerValue.Closed)

    var functionalityNotAvailablePopupShown by remember { mutableStateOf(false) }
    if (functionalityNotAvailablePopupShown) {
//        FunctionalityNotAvailablePopup { functionalityNotAvailablePopupShown = false }
    }

    JetchatAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        onNavIconPressed = onNavIconPressed,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if(searchInputArea){
                    OutlinedTextField(value = searchFieldText , onValueChange = {
                        onSearchQueryChange(it.text)
                        searchFieldText = it
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Cancel,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clickable(onClick = {
                                    searchInputArea = false
                                    searchFieldText = TextFieldValue("")
                                    onSearchQueryChange("")
                                })
                                .padding(horizontal = 12.dp, vertical = 16.dp)
                                .height(24.dp),
                            contentDescription = stringResource(id = R.string.search)
                        )
                    }
                    )

                }else{

                    val eclipse = if(channelName.length > 20) "..." else ""
                    // Channel name
                    Text(
                        text = channelName.substring(0, min(15,channelName.length)) + eclipse,
                        style = MaterialTheme.typography.titleMedium,
                        overflow = TextOverflow.Ellipsis

                    )
                    // Number of members
                    Text(
                        text = stringResource(R.string.members, channelMembers),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            }
        },
        actions = {
            if(!searchInputArea){
                // Search icon
                Icon(
                    imageVector = Icons.Outlined.Search,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clickable(onClick = { searchInputArea = true })
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                        .height(24.dp),
                    contentDescription = stringResource(id = R.string.search)
                )
            }
            // Info icon
            Icon(
                imageVector = Icons.Filled.People,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable(onClick = {
                        drawerOpen.value = true
                    })
                    .padding(horizontal = 12.dp, vertical = 16.dp)
                    .height(24.dp),
                contentDescription = stringResource(id = R.string.info)
            )
        }
    )

    LaunchedEffect(drawerOpen.value){
        if(drawerOpen.value){
            drawerState.open()
            drawerOpen.value = false
        }
    }

    if(drawerState.isOpen){
        ShowMemberDrawer(drawerState = drawerState,memberList)
    }
}

const val ConversationTestTag = "ConversationTestTag"

@Composable
fun Messages(
    messages: List<Message>,
    navigateToProfile: (String) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
    authorMe: String?,
    viewModel: ChatRoomViewModel,
    searchQuery: String,
    onImageClick: (String) -> Unit,
    isChatOver: Boolean,
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()
    var searchComponentCount by remember{ mutableIntStateOf(0) }
    var curSearchIndex by remember{ mutableIntStateOf(0) }
    var rememberSearchComponentIndexList : MutableState<MutableList<Int>> = remember{mutableStateOf(mutableListOf())}

    LaunchedEffect(searchQuery){
        if(searchQuery.isNotEmpty()){
            var index = 0
            var tmpList :MutableList<Int> = mutableListOf()
            messages.forEach{
                if(it.content.contains(searchQuery)){
                    tmpList.add(index)
                }
                index++
            }

            rememberSearchComponentIndexList.value = tmpList.asReversed()
            searchComponentCount = rememberSearchComponentIndexList.value.size

            if(searchComponentCount == 0) {curSearchIndex = 0}
            else{
                curSearchIndex = 1
                scrollState.scrollToItem(rememberSearchComponentIndexList.value[0])
            }
        }

    }


    if(searchQuery.isNotEmpty()){
        val curIndex = (if(searchComponentCount == 0) 0 else curSearchIndex)
        val range = (searchComponentCount).toString()
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(8.dp)),
                horizontalArrangement = Arrangement.End
            ) {
                Text(text = "${curIndex.toString()} / $range" )
                Icon(
                    Icons.AutoMirrored.Outlined.NavigateBefore,
                    contentDescription = "이전",
                    modifier = Modifier.clickable {
                        if(searchComponentCount != 0){
                            curSearchIndex = if(curSearchIndex-1 <= 0) searchComponentCount else curSearchIndex-1
                            scope.launch {  scrollState.scrollToItem(rememberSearchComponentIndexList.value[curSearchIndex-1])}
                        }

                    }
                )
                Icon(
                    Icons.AutoMirrored.Outlined.NavigateNext,
                    contentDescription = "다음",
                    modifier = Modifier.clickable {
                        if(searchComponentCount != 0){
                            curSearchIndex = if(curSearchIndex+1 > searchComponentCount) 1 else curSearchIndex+1
                            scope.launch {  scrollState.scrollToItem(rememberSearchComponentIndexList.value[curSearchIndex-1])}
                        }

                    }
                )
            }
        }
    }




    Box(modifier = modifier) {
        LazyColumn(
            reverseLayout = true,
            state = scrollState,
            modifier = Modifier
                .testTag(ConversationTestTag)
                .fillMaxSize()
        ) {

            for (index in messages.indices) {
                val prevAuthor = messages.getOrNull(index - 1)?.author
                val nextAuthor = messages.getOrNull(index + 1)?.author

                val content = messages[index]

                val isFirstMessageByAuthor = prevAuthor != content.author
                val isLastMessageByAuthor = nextAuthor != content.author

                val prevMsg = messages.getOrNull(index + 1)
                val curMsg = messages.getOrNull(index)

                val itDate = curMsg?.originTime
                val prevDate = prevMsg?.originTime

                var isShowTimeStamp = false

                var formattedDate = compareAndFormatDates(itDate,prevDate)

                if(itDate != null){
//                    if(isFirstMessageByAuthor){

//                    }else{
                        if(prevDate != null) {
                            isShowTimeStamp = isShowTimeStamp(itDate,prevDate)
                        }
//                    }
                }

                item {
                    if(index == 0){
                        if(isChatOver){
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()){
                                HorizontalDivider()
                                Text(text = "종료된 채팅이에요")
                            }
                        }
                    }
                    Message(
                        onAuthorClick = {
                            name ->
                            navigateToProfile(name) },
                        msg = content,
                        isUserMe = content.author == authorMe,
                        isFirstMessageByAuthor = isFirstMessageByAuthor,
                        isLastMessageByAuthor = isLastMessageByAuthor,
                        showTimeStamp = isShowTimeStamp,
                        viewModel = viewModel,
                        searchQuery = searchQuery,
                        onImageClick = onImageClick,
                        onProfileClick = { id ->
                            navController.navigate("PROFILE/${id}")
                        }
                    )

                }

                if(formattedDate.isNotEmpty()){
                    item {
                        DayHeader(formattedDate)

                    }
                }
            }
        }
        // Jump to bottom button shows up when user scrolls past a threshold.
        // Convert to pixels:
        val jumpThreshold = with(LocalDensity.current) {
            JumpToBottomThreshold.toPx()
        }

        // Show the button if the first visible item is not the first one or if the offset is
        // greater than the threshold.
        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex != 0 ||
                        scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }




        JumpToBottom(
            // Only show if the scroller is not at the bottom
            enabled = jumpToBottomButtonEnabled,
            onClicked = {
                scope.launch {
                    scrollState.animateScrollToItem(0)
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}


@Composable
fun Message(
    onAuthorClick: (String) -> Unit,
    msg: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    showTimeStamp: Boolean,
    viewModel: ChatRoomViewModel,
    searchQuery: String,
    onImageClick: (String) -> Unit,
    onProfileClick: (String) -> Unit
) {
    val borderColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    val spaceBetweenAuthors = if (isLastMessageByAuthor) Modifier.padding(top = 8.dp) else Modifier

    Row(modifier = spaceBetweenAuthors) {
        if (isLastMessageByAuthor) {
            val imgSrc = msg.profileImgSrc.run { this  } ?: ""
            AsyncImageWithHeader(
                uri = imgSrc,
                isProfile = true,
                isStudyMember = false,
                isStudyDetail = false,
                onProfileClick = {
                    onProfileClick(msg.id.toString())
                }
            )
        } else {
            Spacer(modifier = Modifier.width(50.dp))
        }
        AuthorAndTextMessage(
            msg = msg,
            isUserMe = isUserMe,
            isFirstMessageByAuthor = isFirstMessageByAuthor,
            isLastMessageByAuthor = isLastMessageByAuthor,
            authorClicked = onAuthorClick,
            showTimeStamp = showTimeStamp,
            searchQuery = searchQuery,
            onImageClick = onImageClick,
            modifier = Modifier
                .padding(end = 16.dp, top = 4.dp)
                .weight(1f)
        )
    }


}


@Composable
fun ImageFromUri(imageUri: String) {
    val painter = rememberImagePainter(imageUri)
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier.fillMaxWidth()
    )

}

@Composable
fun AsyncImageWithHeader(
    uri: String,
    isProfile: Boolean = false,
    isStudyMember: Boolean = false,
    isStudyDetail: Boolean = false,
    onProfileClick: () -> Unit = {},
    onImageClick: (String) -> Unit = {}
) {
    //빈 프로필인경우
    if((isProfile || isStudyMember || isStudyDetail) && uri.isEmpty()){
        val size = if(isStudyDetail) 60.dp else 42.dp
        UnknownMemberImage(modifier = Modifier.size(size), onImageClick = onProfileClick)
        return
    }

    val context = LocalContext.current
    val accessToken = context.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE).getString("accessToken","")
    val refreshToken = context.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE).getString("refreshToken","")

    val serverURL = context.getString(R.string.SERVER_IP)
    val imageUri = "$serverURL/api/img/get.cf?fileName=$uri"

    val request:  @Composable (String, String) -> AsyncImagePainter = { headerKey, headerVal ->
        rememberImagePainter(
            request = ImageRequest.Builder(context)
                .data(imageUri)
                .addHeader(headerKey, headerVal)
                .build()
        )
    }

    var painter = request("Authorization","Bearer $accessToken")
    var isError = painter.state is AsyncImagePainter.State.Error
    if (isError) {
        painter = request("X-REFRESH-TOKEN", "$refreshToken")
    }

    isError = painter.state is AsyncImagePainter.State.Error
    if(isError){
//        Toast.makeText(context, "이미지를 가져오는데 실패하였습니다", Toast.LENGTH_SHORT).show()
    }

    val borderColor = Color.LightGray // 예시로 테두리 색상을 지정, 실제로 사용하는 값으로 대체해야 함

    if(isProfile){
        Image(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(42.dp)
                .border(1.5.dp, borderColor, CircleShape)
//                .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape)
                .clip(CircleShape),
//            .align(Alignment.Top),
            painter = painter,
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
    }
    else if(isStudyMember){
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape),
        )
    }
    else if(isStudyDetail){
        val display = LocalContext.current.applicationContext?.resources?.displayMetrics
        val deviceWidth = display?.widthPixels
        val deviceWidthDp = px2dp1(deviceWidth!!, LocalContext.current)

        val drawable = context.resources.getDrawable(R.drawable.round_menu_book_24, null)
        val bitmap = drawable.toBitmap()

        Box(
            modifier = Modifier
//                .fillMaxWidth()
                .width(deviceWidthDp.dp - 30.dp)
                .height(300.dp)
                .padding(start = 20.dp)
                .clip(shape = RoundedCornerShape(15.dp))
        ) {
            if(uri.contains("default_img.png")){
                val drawable = context.resources.getDrawable(R.drawable.round_menu_book_24, null)
                val bitmap = drawable.toBitmap()
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
            }else{
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                )
            }
        }
    }
    else{
        Image(
            painter = painter,
            contentDescription = null,
//            contentScale = ContentScale.None,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    onImageClick(uri)
                }
            )


    }



}


@Composable
fun AuthorAndTextMessage(
    msg: Message,
    isUserMe: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    authorClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    showTimeStamp: Boolean,
    searchQuery: String,
    onImageClick: (String) -> Unit,
) {

    Column(modifier = modifier) {

        if (isLastMessageByAuthor) {
            AuthorNameTimestamp(msg,true)
        }else if(showTimeStamp){
            AuthorNameTimestamp(msg,false)
        }

        if(msg.content.isNotEmpty()) ChatItemBubble(msg, isUserMe, authorClicked = authorClicked,searchQuery)

        msg.imageList?.let {
            if(msg.imageList.isEmpty()) return

            Box (
                modifier = Modifier.padding(8.dp)
            ){
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .border(1.dp, Color.Black, shape = RoundedCornerShape(8.dp))
//                        .background(Color.DarkGray)
                        .padding(6.dp)
                ){
                    items(it) { imageUri ->
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .padding(4.dp)
                                .clip(shape = RoundedCornerShape(15.dp))
                        ) {
                            AsyncImageWithHeader(
                                uri = imageUri,
                                onImageClick=onImageClick
                            )
                        }
                    }

                }
            }


        }

        if (isFirstMessageByAuthor) {
            // Last bubble before next author
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            // Between bubbles
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}


@Composable
private fun AuthorNameTimestamp(msg: Message, showNickname: Boolean){
    // Combine author and timestamp for a11y.
    Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        if(showNickname){
            Text(
                text = msg.author,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .alignBy(LastBaseline)
                    .paddingFrom(LastBaseline, after = 8.dp) // Space to 1st bubble
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = msg.timestamp,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.alignBy(LastBaseline),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private val ChatBubbleShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)

@Composable
fun DayHeader(dayString: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .height(16.dp)
    ) {
        DayHeaderLine()
        Text(
            text = dayString,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        DayHeaderLine()
    }
}

@Composable
private fun RowScope.DayHeaderLine() {
    Divider(
        modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

@Composable
fun ChatItemBubble(
    message: Message,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit,
    searchQuery: String
) {
    val initColor = if(isUserMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val backgroundColor = remember { mutableStateOf<Color>(initColor) }
    val searchColor = MaterialTheme.colorScheme.outlineVariant

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant

    DisposableEffect(isUserMe){
        if(isUserMe){
            backgroundColor.value = primaryColor
        }else{
            backgroundColor.value = surfaceColor
        }
        onDispose {  }
    }


    DisposableEffect(searchQuery) {
        if(searchQuery.isNotEmpty()){
            val newBackgroundColor : Color = if (message.content.contains(searchQuery)) searchColor  else initColor
            backgroundColor.value = newBackgroundColor
        }else{
            backgroundColor.value = initColor
        }   
        // Clean-up effect
        onDispose { }
    }

    Column {
        Surface(
            color = backgroundColor.value, // Default value
            shape = ChatBubbleShape
        ) {
            ClickableMessage(
                message = message,
                isUserMe = isUserMe,
                authorClicked = authorClicked
            )
        }

        message.image?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = backgroundColor.value, // Default value
                shape = ChatBubbleShape
            ) {
                Image(
                    painter = painterResource(it),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(160.dp),
                    contentDescription = stringResource(id = R.string.attached_image)
                )
            }
        }
    }
}


@Composable
fun ClickableMessage(
    message: Message,
    isUserMe: Boolean,
    authorClicked: (String) -> Unit
) {
    val uriHandler = LocalUriHandler.current

    val styledMessage = messageFormatter(
        text = message.content,
        primary = isUserMe
    )

    ClickableText(
        text = styledMessage,
        style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
        modifier = Modifier.padding(16.dp),
        onClick = {
            styledMessage
                .getStringAnnotations(start = it, end = it)
                .firstOrNull()
                ?.let { annotation ->
                    when (annotation.tag) {
                        SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
                        SymbolAnnotationType.PERSON.name -> {
                            authorClicked(annotation.item)
                        }
                        else -> Unit
                    }
                }
        }
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ChannelBarPrev() {
    JetchatTheme {
    }
}

@Preview
@Composable
fun DayHeaderPrev() {
    DayHeader("Aug 6")
}

private val JumpToBottomThreshold = 56.dp
