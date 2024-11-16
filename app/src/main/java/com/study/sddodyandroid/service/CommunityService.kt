package com.study.sddodyandroid.service

import android.content.Context
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import com.study.sddodyandroid.common.HeartDto
import com.study.sddodyandroid.common.RetrofitInstance
import com.study.sddodyandroid.dto.BoardRequestDto
import com.study.sddodyandroid.dto.BoardResponseDto
import com.study.sddodyandroid.dto.CommentReqestDto
import com.study.sddodyandroid.dto.PageDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream


fun sendBoardCreateRequest(boardRequestDto : BoardRequestDto,
                           sendImageList: MutableState<MutableList<File>>,
                           context : Context,
                           navController: NavHostController){
    val apiService = RetrofitInstance.apiService

    val imageList: List<MultipartBody.Part> = sendImageList.value.map { imageUrl ->
        imageUrl.let {
            val file = it // 이미지 파일 경로로부터 File 객체 생성
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("imageList[]", file.name, requestFile)
            imagePart
        }
    }

    val call = apiService.createBoard(boardRequestDto,imageList)
    call?.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            navController.navigate("mainView")
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Toast.makeText(context, "글쓰기에 실패하였습니다", Toast.LENGTH_SHORT).show()
        }
    })

}

fun sendAddCommentRequest(
    commentRequest: CommentReqestDto,
    navController: NavHostController,
    context: Context){
    val apiService = RetrofitInstance.apiService
    val call = apiService.createComment(commentRequest)
    call?.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            navController.navigate("mainView")
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Toast.makeText(context, "댓글 작성에 실패하였습니다", Toast.LENGTH_SHORT).show()
        }
    })

}

fun sendHeartRequest(
    heartDto: HeartDto,
    context: Context,
    navController: NavHostController
    ){
    val apiService = RetrofitInstance.apiService
    val call = apiService.createHeart(heartDto)
    call?.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if(response.code() == 200) navController.navigate("mainView")
            else Toast.makeText(context, "이미 중복된 추천입니다", Toast.LENGTH_SHORT).show()

        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Toast.makeText(context, "추천에 실패하였습니다", Toast.LENGTH_SHORT).show()
        }
    })
}




fun getStudyBoardList(
    studyId: Long,
    pageDto: PageDto,
    context: Context,
    rememberCommunityContent: MutableState<MutableList<BoardResponseDto>>,
    rememberLoading: MutableState<Boolean>
){
    val apiService = RetrofitInstance.apiService
    val call = apiService.getStudyBoardList(studyId,pageDto.page)
    call?.enqueue(object : Callback<List<BoardResponseDto>> {
        override fun onResponse(
            call: Call<List<BoardResponseDto>>,
            response: Response<List<BoardResponseDto>>
        ) {
            response.body()?.let {
                var sumMutableList :MutableList<BoardResponseDto> =  mutableListOf()
                sumMutableList.addAll(it)
                sumMutableList.addAll(rememberCommunityContent.value)
                rememberCommunityContent.value = sumMutableList
                rememberLoading.value =true
            }
        }

        override fun onFailure(call: Call<List<BoardResponseDto>>, t: Throwable) {
            t.printStackTrace()
            Toast.makeText(context, "커뮤니티를 가져오는데 실패하였습니다", Toast.LENGTH_SHORT).show()
        }
    })
}
fun getBoardList(
    pageDto: PageDto,
    context: Context,
    rememberCommunityContent: MutableState<MutableList<BoardResponseDto>>,
    rememberLoading: MutableState<Boolean>,
    isPortfolio : Boolean = false
){
    val apiService = RetrofitInstance.apiService
    val call = apiService.getBoardList(pageDto.page,isPortfolio = isPortfolio)
    call?.enqueue(object : Callback<List<BoardResponseDto>> {
        override fun onResponse(
            call: Call<List<BoardResponseDto>>,
            response: Response<List<BoardResponseDto>>
        ) {
            response.body()?.let {
                var sumMutableList :MutableList<BoardResponseDto> =  mutableListOf()
                sumMutableList.addAll(it)
                sumMutableList.addAll(rememberCommunityContent.value)
                rememberCommunityContent.value = sumMutableList
                rememberLoading.value =true
            }
        }

        override fun onFailure(call: Call<List<BoardResponseDto>>, t: Throwable) {
            t.printStackTrace()
            Toast.makeText(context, "커뮤니티를 가져오는데 실패하였습니다", Toast.LENGTH_SHORT).show()
        }
    })
}


fun getMemberBoardList(
    memberId : Long,
    context: Context,
    rememberCommunityContent: MutableState<MutableList<BoardResponseDto>>,
    rememberLoading: MutableState<Boolean>,
    isPortfolio : Boolean = false,
    isStudyReview : Boolean = false,
    studyId : Long? = null
){
    val apiService = RetrofitInstance.apiService
    var call : Call<List<BoardResponseDto>>? = null
    if(isStudyReview){
        call = if(studyId == null){
            apiService.getMemberStudyReviewList(memberId = memberId)
        }else{
            apiService.getStudyReviewList(studyId = studyId)
        }
    }else{
        call = apiService.getMemberBoardList(isPortfolio = isPortfolio, memberId = memberId)
    }

    call?.enqueue(object : Callback<List<BoardResponseDto>> {
        override fun onResponse(
            call: Call<List<BoardResponseDto>>,
            response: Response<List<BoardResponseDto>>
        ) {
            response.body()?.let {
                var sumMutableList :MutableList<BoardResponseDto> =  mutableListOf()
                sumMutableList.addAll(it)
                rememberCommunityContent.value = sumMutableList
                rememberLoading.value =true
            } ?: {
                rememberLoading.value =true
            }
        }

        override fun onFailure(call: Call<List<BoardResponseDto>>, t: Throwable) {
            t.printStackTrace()
            Toast.makeText(context, "게시물을 가져오는데 실패하였습니다", Toast.LENGTH_SHORT).show()
        }
    })
}



fun deleteBoard(boardId: Long, context: Context, onDelete: () -> Unit){
    val apiService = RetrofitInstance.apiService
    val call = apiService.deleteBoard(boardId)
    call?.enqueue(object : Callback<Void> {

        override fun onFailure(call: Call<Void>, t: Throwable) {
            makeText(context, "게시물 삭제에 실패하였습니다", Toast.LENGTH_SHORT).show()
        }

        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.code() == 200) {
                onDelete()
            } else {
                Toast.makeText(context, "게시물 삭제에 실패하였습니다", Toast.LENGTH_SHORT).show()

            }
        }
    })
}

fun deleteComment(id : Long, context: Context, onDelete: () -> Unit){
    val apiService = RetrofitInstance.apiService
    val call = apiService.deleteComment(id)
    call?.enqueue(object : Callback<Void> {

        override fun onFailure(call: Call<Void>, t: Throwable) {
            makeText(context, "댓글 삭제에 실패하였습니다", Toast.LENGTH_SHORT).show()
        }

        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.code() == 200) {
                onDelete()
            } else {
                Toast.makeText(context, "댓글 삭제에 실패하였습니다", Toast.LENGTH_SHORT).show()

            }
        }
    })


}

fun modifyBoard(boardId : Long, boardDto : BoardRequestDto, context: Context, navController: NavHostController,
                sendImageList: MutableState<MutableList<File>>,
){

    val imageList: List<MultipartBody.Part> = sendImageList.value.map { imageUrl ->
        imageUrl.let {
            val file = it // 이미지 파일 경로로부터 File 객체 생성
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("imageList[]", file.name, requestFile)
            imagePart
        }
    }

    val apiService = RetrofitInstance.apiService
    val call = apiService.updateBoard(boardId,boardDto,imageList)

    call?.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if(response.code() == 200){
                navController.navigate("mainView")
            }else{
                Toast.makeText(context, "게시물 수정에 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Toast.makeText(context, "게시물 수정에 실패하였습니다", Toast.LENGTH_SHORT).show()

        }
    })



}

/*
수정하는경우를 위해서 board 정보 가져옴
 */

fun getBoard(boardId: Long, rememberBoard: MutableState<BoardResponseDto?>, context : Context){
    val apiService = RetrofitInstance.apiService
    val call = apiService.getBoard(boardId)
    call?.enqueue(object : Callback<BoardResponseDto> {
        override fun onResponse(
            call: Call<BoardResponseDto>,
            response: Response<BoardResponseDto>
        ) {
            response.body()?.let {
                rememberBoard.value = it
            }
        }

        override fun onFailure(call: Call<BoardResponseDto>, t: Throwable) {
            Toast.makeText(context, "게시물을 가져오는데 실패하였습니다", Toast.LENGTH_SHORT).show()
        }
    })

}





fun imageSrcToFile(
    fileName: String,
    context: Context,
    imageFileList: MutableState<MutableList<File>>
)
{
    val apiService = RetrofitInstance.apiService
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
//                    imageFileList.add(file)
                    // 파일을 성공적으로 저장한 후 반환
                }
            }
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        }

    })
}



