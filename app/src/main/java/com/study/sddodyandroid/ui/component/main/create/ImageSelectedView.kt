package com.study.sddodyandroid.ui.component.main.create

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.study.sddodyandroid.R
import com.study.sddodyandroid.common.CreateStudyViewModel
import com.study.sddodyandroid.service.getMultipleGalleryLauncher

@SuppressLint("MutableCollectionMutableState")
@Composable
fun SetStudyImage(
    @StringRes titleResourceId: Int,
    imageUri:  MutableList<Uri>,
    viewModel: CreateStudyViewModel,
    getNewImageUri: () -> Uri,
    onPhotoTaken: (MutableList<Uri>) -> Unit,
    modifier: Modifier,
) {
    var rememberImageList = remember{ mutableStateOf(viewModel.imageUriList) }

    var galleryLauncher = getMultipleGalleryLauncher(context = LocalContext.current,onSubmit = {
//        viewModel.imageUriList = it
        rememberImageList.value = it
        viewModel.onImgResponse(it)
    })

    QuestionWrapper(
        titleResourceId = titleResourceId,
        modifier = modifier.verticalScroll(rememberScrollState()),
    ){
        Column {
            OutlinedButton(
                onClick = {
                    //newImageUri = getNewImageUri()
                    galleryLauncher.launch("image/*")
                    //                Log.d("debug", newImfageUri.toString())
                },
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.BottomCenter)
                            .padding(vertical = 26.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Filled.AddAPhoto, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(
                                R.string.add_photo
                            )
                        )
                    }
                }
            }

            StudyLazySelectedImage(rememberImageList, viewModel)
        }
    }
}

@Composable
fun StudyLazySelectedImage(
    uploadImageUriList: MutableState<MutableList<Uri>>,
    viewModel: CreateStudyViewModel
) {
    Box {
        if (uploadImageUriList.value.isNotEmpty()) {
            Log.d("debug", "${viewModel.imageUriList.size}")
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .border(1.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(8.dp)
            ) {
                items(uploadImageUriList.value) { imageUri ->
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .height(150.dp)
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
                                viewModel.imageUriList = uploadImageUriList.value
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
