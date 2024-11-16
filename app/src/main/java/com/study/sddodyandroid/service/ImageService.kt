package com.study.sddodyandroid.service

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.study.sddodyandroid.common.AuthorizationCallBack
import com.study.sddodyandroid.common.RetrofitInstance
import com.study.sddodyandroid.dto.FirebaseSendDto
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

fun resourceIdToUri(context: Context, resourceId: Int): Uri {
    return Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .authority(context.resources.getResourcePackageName(resourceId))
        .appendPath(context.resources.getResourceTypeName(resourceId))
        .appendPath(context.resources.getResourceEntryName(resourceId))
        .build()
}
@Composable
fun getMultipleGalleryLauncher(
    context: Context,
    onSubmit: (MutableList<Uri>) -> Unit
): ManagedActivityResultLauncher<String, List<@JvmSuppressWildcards Uri>> {
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uriList ->
            if (uriList.size > 10) {
                Toast.makeText(context, "이미지 개수는 최대 10개입니다", Toast.LENGTH_SHORT).show()
                return@rememberLauncherForActivityResult
            }
            val maxSizeBytes = 10 * 1024 * 1024 // 10MB 제한

            // 이미지 파일의 크기 확인
            for (uri in uriList) {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val curSize = inputStream.available().toLong()

                    // 이미지 크기가 제한을 초과하면 사용자에게 알림
                    if (curSize > maxSizeBytes) {
                        Toast.makeText(context, "이미지 크기가 너무 커요.", Toast.LENGTH_SHORT).show()
                        return@rememberLauncherForActivityResult
                    }
                }
            }

            onSubmit(uriList.toMutableList())
        }
    )
    return galleryLauncher
}



@Throws(IOException::class)
fun uriToFile(context: Context, uri: Uri): File? {
    val contentResolver: ContentResolver = context.contentResolver
    val inputStream: InputStream? = contentResolver.openInputStream(uri)
    val file = File(uri.path)
    val type = contentResolver.getType(uri)
    var extension = "jpg"
//    val extension = type?.split("/")?.get(1)
    type?.let {
        val splitArr = it.split("/")
        extension = splitArr[splitArr.size-1]
    }
    val fileName = "${file.name}.$extension"

    inputStream?.use { input ->
        val outputFile = File(context.externalCacheDir,fileName)
        FileOutputStream(outputFile).use { output ->
            val buffer = ByteArray(4 * 1024)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                output.write(buffer, 0, read)
            }
            output.flush()
            return outputFile
        }
    }
    return null
}

fun getResourceUri(context: Context, resId: Int): Uri {
    val resources = context.resources
    return Uri.parse(
        ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + resources.getResourcePackageName(resId)
                + '/' + resources.getResourceTypeName(resId)
                + '/' + resources.getResourceEntryName(resId)
    )
}


