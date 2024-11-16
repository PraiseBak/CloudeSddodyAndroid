package com.study.sddodyandroid.component.chatroom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.rememberNavController
import com.study.sddodyandroid.R
import com.study.sddodyandroid.component.conversation.AsyncImageWithHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL


@Composable
fun ImageZoom(
    imageUri: String?,
    backStackEntry: NavBackStackEntry,
    modifier: Modifier = Modifier
) {

    val navController = rememberNavController()
    val context = LocalContext.current
    val accessToken = context.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE).getString("accessToken","")
    val refreshToken = context.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE).getString("refreshToken","")
    imageUri?.let { AsyncImageWithHeader(uri = it) }

    // 뒤로가기 버튼을 클릭했을 때 이전 화면으로 이동하는 함수
    val onBackClick: () -> Unit = {
        navController.navigateUp()
    }

    // 뒤로가기 버튼
    IconButton(onClick = onBackClick) {
        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
    }

    var isLoad = remember{ mutableStateOf(false) }
    var bitmap :MutableState<Bitmap?> = remember{ mutableStateOf(null)}

    GetImageBitmap(imageUri, bitmap, accessToken, isLoad, refreshToken)

    if(isLoad.value){
        IconButton(onClick = {
            bitmap.value?.let { it1 -> saveImageToGallery(bitmap = it1, displayName = "sddody_$imageUri",context=context) }
        }) {
            Icon(Icons.Default.Save, contentDescription = "저장")
        }
    }

}

@Composable
fun GetImageBitmap(
    imageUri: String?,
    bitmap: MutableState<Bitmap?>,
    accessToken: String?,
    isLoad: MutableState<Boolean>,
    refreshToken: String?,
    iconList: MutableList<Bitmap>? = null
){
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            val serverURL = context.getString(R.string.SERVER_IP)
            try {
                val fullUri = "$serverURL/api/img/get.cf?fileName=$imageUri"
                bitmap.value = connect("Authorization", "Bearer $accessToken", fullUri)
                isLoad.value = true
                if(iconList != null){
                    bitmap.value?.let { iconList.add(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                try {
                    val fullUri = "$serverURL/api/img/get.cf?fileName=$imageUri"
//                    val fullUri = "http://10.0.2.2:8080/api/img/get.cf?fileName=$imageUri"
                    bitmap.value = connect("X-REFRESH-TOKEN", "$refreshToken", fullUri)
                    isLoad.value = true
                } catch (e: Exception) {
                    println("zoom 실패")
                }
            }

        }
    }
}



fun connect(key : String,value : String,fullUri : String): Bitmap? {
    var bitmap: Bitmap? = null
    val url = URL(fullUri)

    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
    connection.setRequestProperty(key,value) // 헤더 추가
    connection.connect()

    val inputStream = connection.inputStream
    bitmap = BitmapFactory.decodeStream(inputStream)
    return bitmap
}


fun saveImageToGallery(bitmap: Bitmap, displayName: String, mimeType: String = "image/jpeg",context : Context) {

    // 이미지를 저장할 수 있는 외부 저장소 디렉터리를 가져옴
    val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val image = File(imagesDir, "$displayName.jpg")

    // 이미지를 저장할 OutputStream을 열고 비트맵을 JPEG 파일로 저장
    var outputStream: OutputStream? = null
    try {
        outputStream = FileOutputStream(image)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        // 갤러리에 이미지를 추가
        MediaStore.Images.Media.insertImage(
            context.contentResolver,
            image.absolutePath,
            displayName,
            ""
        )

        // 이미지 저장 성공 메시지 표시
        Toast.makeText(context, "이미지가 저장되었습니다.", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        // 이미지 저장 실패 메시지 표시
        Toast.makeText(context, "이미지 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    } finally {
        outputStream?.close()
    }
}