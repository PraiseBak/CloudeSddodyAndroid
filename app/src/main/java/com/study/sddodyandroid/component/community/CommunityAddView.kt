package com.study.sddodyandroid.component.community

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.common.RetrofitInstance
import com.study.sddodyandroid.component.conversation.LazySelectedImage
import com.study.sddodyandroid.dto.BoardRequestDto
import com.study.sddodyandroid.dto.BoardResponseDto
import com.study.sddodyandroid.service.getBoard
import com.study.sddodyandroid.ui.common.CommonBackgroundColor
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

/*
게시물 수정은 onModify로 구분합니다
 */
@Composable
fun CommunityAddView(
    navController: NavHostController,
    rememberImageUriList: MutableState<MutableList<File>>,
    modifier: Modifier = Modifier,
    onImageUpload: () -> Unit,
    onWriteSubmit: (BoardRequestDto) -> Unit,
    modifyBoardId : Long? = null,
    studyId: Long? = null,
    isPortfolio : Boolean = false
) {

    //수정용
    val rememberBoard = remember { mutableStateOf<BoardResponseDto?>(null) }
    val rememberTitle = remember { mutableStateOf("") }
    val rememberContent = remember { mutableStateOf("") }
    val rememberLink = remember { mutableStateOf("") }
    val rememberErrorMsg = remember { mutableStateOf("") }
    val rememberTagList = remember { mutableStateListOf<String>() }
    val context = LocalContext.current

    modifyBoardId?.let {
        getBoard(modifyBoardId,rememberBoard, context = context)
    }


    rememberBoard.value?.let {
        rememberTitle.value = it.title
        rememberContent.value = it.content
        val imageFileList : MutableList<File>  = mutableListOf()
        setImageSrcListToFileList(it.imageSrcList,rememberImageUriList,context)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
    )
    {

        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
            .padding(10.dp)) {

            items(1) {

                CommunityContentInputArea(
                    labelText = "제목",
                    placeholderText = "",
                    text = rememberTitle.value,
                    onTextChanged = { rememberTitle.value = it },
                    minHeight = 8,
                    inputType = InputType.Title,
                    maxTextLength = 40
                )

                Spacer(modifier = Modifier.height(8.dp))


                CommunityContentInputArea(
                    labelText = "내용",
                    placeholderText = if(studyId != null) "스터디를 진행하며 좋았던 점과 결과물에 대해서 작성해주세요" else "",
                    text = rememberContent.value,
                    minHeight = 160,
                    inputType = InputType.Content,
                    onTextChanged = { rememberContent.value = it },
                    maxTextLength = 4000

                )


                CommunityContentInputArea(
                    labelText = "연관 링크",
                    placeholderText = "관련된 링크를 넣어주세요 (http//.,https// 생략 가능)",
                    text = rememberLink.value,
                    minHeight = 8,
                    inputType = InputType.Link,
                    onTextChanged = { rememberLink.value = it },
                    maxTextLength = 4000
                )


                //태그 기능 추가
                AddTagList(rememberTagList)

                //포트폴리오면 링크 추가?
                if(isPortfolio){
                }else{
                    if(rememberErrorMsg.value.isNotBlank()){
                        Text(text = rememberErrorMsg.value)
                    }
                }

                LazySelectedImage(uploadImageUriList = rememberImageUriList, color = Color.White)


                Row(
                    modifier = Modifier
                        .padding(top = 14.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End // 오른쪽 정렬 설정
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(4.dp) // IconButton과 배경 사이의 간격 조정
                    ) {
                        Row() {
                            IconButton(onClick = { onImageUpload() }

                            ) {
                                Icon(Icons.Outlined.Photo, contentDescription = "이미지 업로드")
                            }

                            IconButton(onClick = {
                                val boardRequestDto = BoardRequestDto(
                                    title = rememberTitle.value,
                                    content = rememberContent.value,
                                    isPortfolio = isPortfolio,
                                    tagList = rememberTagList,
                                    link = rememberLink.value
                                )

                                //스터디 id있을시 추가
                                studyId?.let{
                                    boardRequestDto.boardTargetStudyId = it
                                }


                                val errorMsg = boardRequestDto.isValid()
                                if (errorMsg.isNotEmpty()) {
                                    rememberErrorMsg.value = errorMsg
                                } else {
                                    onWriteSubmit(boardRequestDto)
                                }
                            }) {
                                Icon(Icons.Outlined.Check, contentDescription = "글쓰기")
                            }
                        }

                    }

                }

            }
        }

    }

}


@Composable
fun AddTagList(rememberTagList: SnapshotStateList<String>) {

    val rememberTagValue = remember{ mutableStateOf("") }
    var isInputTagOn = remember{ mutableStateOf(false) }

    val context = LocalContext.current

    Text(text = "태그",
        style = MaterialTheme.typography.bodyLarge)

    IconButton(
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = CommonBackgroundColor(),
            contentColor = Color.DarkGray
        ),
        onClick = {
        if(!isInputTagOn.value) {
            isInputTagOn.value = true
            return@IconButton
        }
        val text = rememberTagValue.value
        if(rememberTagList.size > 10){
            Toast.makeText(context, "태그는 최대 10개만 가능해요", Toast.LENGTH_SHORT).show()
        }
        else if(text.length < 2 || text.length > 40 ){
            Toast.makeText(context, "태그는 2글자 이상 40자로 작성해주세요", Toast.LENGTH_SHORT).show()

        }else{
            rememberTagList.add(rememberTagValue.value)
            rememberTagValue.value = ""
        }
    }) {
        Icon(Icons.Rounded.Tag, contentDescription = "태그 생성")
    }



    LazyRow(modifier = Modifier.padding(4.dp)){
        //입력부분
        item{
            if(isInputTagOn.value){
                TagInputArea(
                    placeholderText = "태그",
                    onAdd = {it -> rememberTagList.add(it)},
                    rememberText = rememberTagValue
                )
            }

            rememberTagList.forEachIndexed{ index,it ->
                Box()
                {
                    Button(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .height(56.dp),
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(Color.Gray), // 배경색 설정
                    ) {
                        Text(text = "#$it")
                        Spacer(modifier = Modifier.weight(2f))
                    }

                    IconButton(
                        modifier = Modifier.align(Alignment.TopEnd),
                        onClick = {
                            rememberTagList.removeAt(index)
                        }) {
                        Icon(modifier = Modifier.padding(start=24.dp, bottom = 16.dp,end = 6.dp),
                            imageVector = Icons.Outlined.Close, contentDescription = "태그 삭제")
                    }
                }
            }


        }

    }





}


@Composable
fun TagInputArea(
    placeholderText: String,
    onAdd: (String) -> Unit,
    rememberText: MutableState<String>,

    ){

    OutlinedTextField(
        placeholder = { Text("tag") }, // Placeholder 추가
        onValueChange = {it -> rememberText.value = it},
        value = rememberText.value,
        modifier = Modifier
            .width(100.dp)
            .height(56.dp)
            .background(Color.White, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp), // 모서리 둥글게 만들기
        prefix = {Text("#")}

    )






}

fun setImageSrcListToFileList(imageSrcList: List<String>, rememberImageUriList: MutableState<MutableList<File>>, context: Context) {
    val apiService = RetrofitInstance.apiService

    val mutableFileList : MutableList<File> = mutableListOf()

    imageSrcList.forEach{fileName ->
        val call = apiService.downloadImage(fileName)
        call?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        // 파일로 저장
                        val file = File(context.filesDir, fileName)
                        val outputStream = FileOutputStream(file)
                        outputStream.use { output ->
                            output.write(body.bytes())
                        }
                        mutableFileList.add(file)

                        if(imageSrcList.size == mutableFileList.size){
                            rememberImageUriList.value = mutableFileList.toMutableList()
                         }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }

        })
    }
}
