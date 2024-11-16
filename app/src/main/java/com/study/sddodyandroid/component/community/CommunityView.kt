package com.study.sddodyandroid.component.community

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.common.HeartDto
import com.study.sddodyandroid.component.chatroom.LoadingIndicator
import com.study.sddodyandroid.component.conversation.AsyncImageWithHeader
import com.study.sddodyandroid.dto.BoardResponseDto
import com.study.sddodyandroid.dto.CommentReqestDto
import com.study.sddodyandroid.dto.CommentResponseDto
import com.study.sddodyandroid.dto.PageDto
import com.study.sddodyandroid.service.compareAndFormatDates
import com.study.sddodyandroid.service.deleteBoard
import com.study.sddodyandroid.service.deleteComment
import com.study.sddodyandroid.service.formatDate
import com.study.sddodyandroid.service.getBoardList
import com.study.sddodyandroid.service.getStudyBoardList
import com.study.sddodyandroid.service.sendAddCommentRequest
import com.study.sddodyandroid.service.sendHeartRequest
import com.study.sddodyandroid.ui.common.CommonBackgroundColor
import com.study.sddodyandroid.ui.component.study.EmptyComingSoon
import java.time.Instant
import java.util.Date

/*
메인뷰에서 3개만 출력하는 경우 isPreview를 사용
 */
@Composable
fun CommunityView(navController: NavHostController,
                  isPreview : Boolean = false,
                  onChangeCommunityViewClick : () -> Unit = {},
                  modifier : Modifier = Modifier,
                  studyId: Long = -1L) {
    val cardHeight = 0.4f

    val rememberPaging = remember{ mutableStateOf(PageDto(page = 1))}
    val rememberCommunityContent : MutableState<MutableList<BoardResponseDto>> = remember { mutableStateOf(mutableListOf()) }
    val context = LocalContext.current
    val rememberLoading =  remember{ mutableStateOf(false)}

    LaunchedEffect(rememberPaging){
        if(studyId == -1L){
            getBoardList(pageDto = rememberPaging.value,context,rememberCommunityContent,rememberLoading)
        }else{
            getStudyBoardList(studyId,pageDto = rememberPaging.value,context,rememberCommunityContent,rememberLoading)
        }
    }


    if(rememberLoading.value){
        Spacer(modifier = Modifier.height(20.dp))

        if(isPreview){
            CommunityContentCardList(
                navController = navController,
                rememberCommunityContent = rememberCommunityContent.value.subList(0,Math.min(3,rememberCommunityContent.value.size)),
                onMoreContentClick ={ onChangeCommunityViewClick()},
                isPreview = isPreview,
                noCommentInput = false
            )

        }else{
            CommunityContentCardList(
                navController = navController,
                rememberCommunityContent = rememberCommunityContent.value,
                onMoreContentClick ={ onChangeCommunityViewClick() },
                isPreview = isPreview,
                noCommentInput = false
            )
        }

        if(rememberCommunityContent.value.size == 0){
            Column(modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EmptyComingSoon("아직 작성된 게시물이 없어요")
            }
        }

    }else{
        //로딩창
        LoadingIndicator()
    }
}

enum class InputType {
    Comment,
    Title,
    Content,
    Link
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CommunityContentCardList(
    navController: NavHostController,
    rememberCommunityContent: MutableList<BoardResponseDto>,
    onMoreContentClick: () -> Unit = {},
    isPreview: Boolean = false,
    noCommentInput: Boolean = false
) {
    val rememberPaging = remember { mutableStateOf(PageDto(page = 1)) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE)
    val myNickname = sharedPreferences.getString("nickname", "")


    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
//        containerColor =  Color.LightGray.alpha,
//            containerColor = Color(127, 217, 213),
            containerColor = Color.White,
            )
    )
    {
        Column {

            rememberCommunityContent.forEach {
                Box(modifier = Modifier
                    .padding())
                {
                    Column(modifier = Modifier.padding(top = 20.dp)){
                        CommunityContentCard(it, navController, context, myNickname, noCommentInput)
                    }
                }
            }
        }
    }
}

// 웹 링크를 여는 함수
fun openWebLink(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "url이 유효하지 않습니다", Toast.LENGTH_SHORT).show()
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CommunityContentCard(
    it: BoardResponseDto,
    navController: NavHostController,
    context: Context,
    myNickname: String?,
    noAdditionalInput: Boolean = false,
    isPortfolio : Boolean = false,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        val rememberCommentError = remember { mutableStateOf("") }
        val rememberComment = remember { mutableStateOf("") }
        val imgSrc = it.profileImageSrc
//        val imgSrc = "https://ghchart.rshah.org/jamesujeon"

        Row(modifier = Modifier.padding(top = 10.dp)) {
            AsyncImageWithHeader(uri = imgSrc, true, false, false,
                onProfileClick = {
                    navController.navigate("PROFILE/${it.memberId}")
                })
            Text(text = it.nickname,
                modifier = Modifier.padding(start = 0.dp,top = 8.dp)
                )

            Spacer(modifier = Modifier.weight(4f)) // 기존 버튼 아래에 여백을 추가합니다.

            Box(){
                ExpandableDropdownMenu(deleteId = it.id,
                    onDelete = { navController.navigate("mainView") },
                    onModify = {
                        navController.navigate("community/modify/${it.id}")
                    }
                )
            }
        }
        Column (modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp)){

            Text(modifier= Modifier
                .align(Alignment.End)
                .padding(end = 8.dp),
                style = MaterialTheme.typography.labelSmall,
                text = compareAndFormatDates(
                    it.updatedAt,
                    Date.from(Instant.now())
                ) + " " + formatDate(it.createdAt)
            )
        }

        //내용부분
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 10.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
        ) {
            CommunityTextArea(text = it.title, minHeight = 8.dp
                ,textStyle = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            ExpandableText(
                text = it.content,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .heightIn(8.dp)
            )

            LazyRow(modifier = Modifier.padding(top = 12.dp)){
                item(){
                    ShowTagList(tagList = it.tagList)
                }
            }
            //링크
            if(it.link.isNotBlank()){
                val url = it.link
                var convertedUrl = url
                if(!url.contains("http://") && !url.contains("https://")){
                    convertedUrl = "http://$convertedUrl"
                }

                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 12.dp)) {
                    IconButton(onClick = { openWebLink(context,convertedUrl) }) {
                        Icon(
                            imageVector = Icons.Default.Link, contentDescription = "링크")
                    }
                    Text(text = convertedUrl)
                }



            }




        }


        //이미지
        Box(
            modifier = Modifier.padding(8.dp)
        ) {
            if (it.imageSrcList.isEmpty()) return@Box
            HorizontalDivider()
//                        Icon(imageVector = Icons.Default.Image, contentDescription = "이미지 부분",modifier = Modifier.padding(horizontal = 4.dp))

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
//                                .border(1.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                    //                        .background(Color.DarkGray)
                    .padding(6.dp)
            ) {

                items(it.imageSrcList) { imageUri ->
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                            .padding(top = 4.dp, start = 4.dp)
                            .clip(shape = RoundedCornerShape(15.dp))
                    ) {
                        AsyncImageWithHeader(
                            uri = imageUri,
                            onImageClick = { imageUri ->
                                navController.navigate("image/zoom/$imageUri")
                            }
                        )
                    }
                }
            }
        }


        if(!noAdditionalInput){
            //상호작용 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End

            ) {
                Icon(Icons.Outlined.ThumbUp, contentDescription = "좋아요",
                    modifier = Modifier
                        .clickable {
                            sendHeartRequest(
                                HeartDto(boardId = it.id),
                                context,
                                navController
                            )
                        }
                )

                Text(
                    text = it.heartCount.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.width(4.dp)) // 아이콘과 텍스트 사이에 간격을 추가합니다.

                Icon(Icons.AutoMirrored.Outlined.Comment, contentDescription = "댓글")
                Text(
                    text = it.commentList.size.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.width(4.dp)) // 아이콘과 텍스트 사이에 간격을 추가합니다.
            }
        }





        //댓글
        CommentArea(it.commentList, myNickname!!, navController)

        if(!noAdditionalInput){

            if (rememberCommentError.value.isNotBlank()) {
                Text(modifier = Modifier.padding(top=4.dp), text = rememberCommentError.value)
            }


            Box(modifier = Modifier.padding(4.dp)) {
                CommunityContentInputArea(
                    placeholderText = "댓글",
                    text = rememberComment.value,
                    minHeight = 8,
                    inputType = InputType.Comment,
                    onTextChanged = { rememberComment.value = it },
                    onSendComment = { text ->
                        val commentRequestDto =
                            CommentReqestDto(boardId = it.id, content = text)
                        if (!commentRequestDto.isValid()) rememberCommentError.value =
                            "댓글은 2-200자로 입력해주세요"
                        else sendAddCommentRequest(
                            commentRequestDto,
                            navController,
                            context
                        )
                    }
                )
            }
        }


    }

}

@Composable
fun ShowTagList(tagList: List<String>) {
        //태그부분
        tagList.forEach{ it ->
            Text(modifier = Modifier.padding(start = 2.dp,top = 8.dp),
                text = "#$it",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.DarkGray)


    }


}

@Composable
fun FooterArea(onMoreContentClick: () -> Unit) {
    Column(modifier = Modifier
        .padding(horizontal = 20.dp)
        .fillMaxWidth()) {

            Text(modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(4.dp)
                .heightIn(min = 4.dp)
                .clickable { onMoreContentClick() },
                text="더보기")
        }

}

@Composable
fun ExpandableDropdownMenu(onDelete: () -> Unit, deleteId: Long,
                           onModify: () -> Unit = {},
                           isComment: Boolean = false) {
    var expandedMenu by remember { mutableStateOf(false) }
    var showAlertDialog = remember { mutableStateOf(false) }


    Box() {
        IconButton(
            onClick = { expandedMenu = !expandedMenu },
        ) {
            Icon(Icons.Outlined.MoreVert, contentDescription = "상호작용 버튼", tint = Color.DarkGray)
        }
        DropdownMenu(
            expanded = expandedMenu,
            onDismissRequest = { expandedMenu = false },
            modifier = Modifier
                .background(Color.White)
                .padding(8.dp)
        ) {
            if(!isComment){
                DropdownMenuItem(text = {Text("수정")}, onClick = { onModify() })
            }
            DropdownMenuItem(text = {Text("삭제")}, onClick = { showAlertDialog.value = true})
        }
        if(showAlertDialog.value) DeleteAlertDialog(deleteId,showAlertDialog,onDelete,isComment )


    }

}

@Composable
fun DeleteAlertDialog(
    deleteId: Long,
    showAlertDialog: MutableState<Boolean>,
    onDelete: () -> Unit,
    isComment: Boolean
){
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = {
            // 다이얼로그를 닫음
            showAlertDialog.value = false // 다이얼로그를 닫음

        },
        title = {
            Text("게시물 삭제")
        },
        text = {
            Text("정말로 삭제하시겠습니까?")
        },
        confirmButton = {
            Button(
                onClick = {
                    // '예' 버튼을 클릭했을 때의 동작
                    showAlertDialog.value = false // 다이얼로그를 닫음
                    if(isComment){
                        deleteComment(deleteId,context,onDelete)
                    }else{
                        deleteBoard(deleteId, context,onDelete)
                    }
                }
            ) {
                Text("삭제")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    // '아니오' 버튼을 클릭했을 때의 동작
                    showAlertDialog.value = false // 다이얼로그를 닫음
                }
            ) {
                Text("취소")
            }
        }
    )
}

@Composable
fun ExpandableText(text: String, modifier: Modifier) {
    var isExpanded by remember { mutableStateOf(false) }

    Column {
        val displayText = if (text.length > 400 && !isExpanded) {
            text.substring(0, 400) // 400자 이후의 글자는 잘라서 표시
        } else {
            text
        }
        Text(
            text = displayText,
            modifier = modifier
        )


        if (text.length > 400 && !isExpanded) {
            IconButton(
                onClick = { isExpanded = true },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(4.dp)
            ) {
                Icon(Icons.Outlined.MoreHoriz, contentDescription = "더 보기", tint = Color.Gray)
            }
        } else if (text.length > 400 && isExpanded) {
            IconButton(
                onClick = { isExpanded = false },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(4.dp)
            ) {
                Icon(Icons.Outlined.ExpandLess, contentDescription = "간략히 보기", tint = Color.Gray)
            }
        }
    }
}

//코멘트 부분
@Composable
fun CommentArea(
    commentList: MutableList<CommentResponseDto>,
    myNickname: String,
    navController: NavHostController
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .padding(start=4.dp,end=4.dp)){

        commentList.forEachIndexed{index, it->
            if(index >= 3 && !isExpanded) return@forEachIndexed

            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
            /*    .background(
                    //자기 닉네임이면 색깔 다르게
                    if (myNickname == it.nickname) Color.Gray else Color.DarkGray,
                    RoundedCornerShape(8.dp)
                )
             */
            ) {
                Row(modifier = Modifier
                    .padding(4.dp)
                    .padding(end = 40.dp)
                ){
                    AsyncImageWithHeader(
                        uri = it.userProfileImageSrc,
                        true,
                        false,
                        false
                    ) {}
                    Column(modifier = Modifier
                        .padding(4.dp)
                    ) {
                        Text(text = it.nickname)
                        Text(
                            modifier = Modifier.padding(top=4.dp),
                            text = it.content)
                    }
                    Spacer(modifier = Modifier.weight(1f)) // 기존 버튼 아래에 여백을 추가합니다.
                }
                HorizontalDivider()

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(text = compareAndFormatDates(it.updatedAt,Date.from(Instant.now())) + " " + formatDate(it.updatedAt))
                    ExpandableDropdownMenu(deleteId = it.id, onDelete = { navController.navigate("mainView")},
                        isComment = true
                    )
                }
            }
        }

        //더보기
        if(commentList.size > 3){
            if (!isExpanded) {
                IconButton(
                    onClick = { isExpanded = true },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(4.dp)
                ) {
                    Icon(Icons.Outlined.MoreHoriz, contentDescription = "더 보기", tint = Color.Gray)
                }
            } else if (isExpanded) {
                IconButton(
                    onClick = { isExpanded = false },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(4.dp)
                ) {
                    Icon(Icons.Outlined.ExpandLess, contentDescription = "간략히 보기", tint = Color.Gray)
                }
            }
        }
    }
}


@Composable
fun CommunityTextArea(
    text: String,
    minHeight: Dp,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium
){
    Text(
        text = text,
        style = textStyle,
        modifier = Modifier
//            .background(Color.Gray, RoundedCornerShape(8.dp))
            .padding(top = 8.dp, start = 8.dp)
            .fillMaxWidth()
            .heightIn(minHeight)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityContentInputArea(
    maxTextLength : Int = 0,
    placeholderText : String = "",
    text: String,
    minHeight: Int,
    inputType: InputType,
    onTextChanged: (String) -> Unit,
    onSendComment : (String) -> Unit = {},
    labelText : String = "",
) {

    Text(text = labelText,
        style = MaterialTheme.typography.bodyLarge)

    OutlinedTextField(
        placeholder = { Text(placeholderText) }, // Placeholder 추가
        value = text,

        supportingText = {
            if(InputType.Comment != inputType && InputType.Link != inputType){
                Text(
                    text = "${text.length} / $maxTextLength",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            }
        },

        onValueChange = { newText ->
            val maxLen = when(inputType){
                InputType.Content -> 4000
                InputType.Comment -> 200
                InputType.Title -> 40
                InputType.Link -> 200
            }
            if (newText.length <= maxLen) onTextChanged(newText)

        },

        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = minHeight.dp),

        textStyle = MaterialTheme.typography.bodyMedium,
        trailingIcon =  { if(inputType == InputType.Comment){ Icon(imageVector = Icons.Filled.Email,contentDescription = "전송",
            Modifier.clickable { onSendComment(text) }) } }
    )


}


