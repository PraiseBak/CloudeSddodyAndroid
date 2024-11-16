package com.study.sddodyandroid.component.start


import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.kakao.sdk.common.KakaoSdk.type
import com.study.sddodyandroid.R
import com.study.sddodyandroid.common.CheckedEnumViewModel
import com.study.sddodyandroid.helper.member.DevLevelEnum
import com.study.sddodyandroid.helper.member.DevYearEnum
import com.study.sddodyandroid.service.getResourceUri
import com.study.sddodyandroid.ui.theme.slightlyDeemphasizedAlpha
import setTitleResource

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SetDevIntroduce(
    navController: NavHostController,
    viewModel: CheckedEnumViewModel,
){
    val context = LocalContext.current
    val defaultImageUri: Uri = getResourceUri(context,R.drawable.baseline_photo_camera_24)
    viewModel.profileUri = defaultImageUri
    var selectedImageUri by remember { mutableStateOf(viewModel.profileUri) }
    var devLevelSelectIdx by remember { mutableIntStateOf(0) }
    var devYearSelectIdx by remember { mutableIntStateOf(0) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedImageUri = it }
    }

    Log.d("debug","SetDevIntroduce")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var selfIntroduction by remember { viewModel.selfIntroduce }
        var nickname by remember { viewModel.nickname }
        var maxNicknameNum = 20
        var maxSelfIntroduce = 200
        var devYear by remember { viewModel.rememberDevYear}
        var devLevel by remember { viewModel.rememberDevLevel}


        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .weight(1f)
        ) {
            LazyColumn {

                item {

                    Text(
                        text = "당신에 대해 알려주세요",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.inverseOnSurface,
                                shape = MaterialTheme.shapes.large
                            )
                            .padding(vertical = 24.dp, horizontal = 16.dp)
                    )


                    Column(modifier = Modifier.fillMaxWidth()
                        , horizontalAlignment = Alignment.CenterHorizontally) {
                        // Centered Image
                        AsyncImage(
                            model = if(selectedImageUri == null) defaultImageUri else selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier
                                .clickable { launcher.launch("image/*") }
                                .size(120.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(Color.LightGray)
                                .padding(8.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))


                    TextField(
                        value = nickname,
                        onValueChange = {
                            if (it.length <= maxNicknameNum) {
                                nickname = it
                            }

                        },
                        label = { Text("닉네임") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                // Handle the done action (e.g., submit the form)
                                // You can also close the keyboard programmatically if needed.
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth(),
                        supportingText = {
                            Text(
                                text = "${nickname.length} / ${maxNicknameNum}",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = selfIntroduction,
                        onValueChange = {
                            if (it.length <= maxSelfIntroduce) {
                                selfIntroduction = it
                            }
                        },
                        label = { Text("자기소개") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                // Handle the done action (e.g., submit the form)
                                // You can also close the keyboard programmatically if needed.
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(1f / 2f),
                        supportingText = {
                            Text(
                                text = "${selfIntroduction.length} / ${maxSelfIntroduce}",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                            )
                        }
                    )

                    //개발 구력
                    Text(text = "개발, 시작한지 얼마나 됐나요?")
                    HorizontalDivider()
                    DevYearEnum.entries.forEachIndexed { index, it ->
                        Button(
                            onClick = {
                                viewModel.rememberDevYear.value = it
                                devYearSelectIdx = index
                            },
                            colors = ButtonColors(
                                containerColor = if (index == devYearSelectIdx) {
                                    Color(37,137,143)
                                } else {
                                    Color(107,197,193)
                                },
                                contentColor = Color.White,
                                disabledContainerColor = MaterialTheme.colorScheme.outline,
                                disabledContentColor = Color.White
                            )
                        ) {
                            Text(it.text)
                        }
                    }

                    //개발 수준
                    Text(text = "당신은? ...")
                    HorizontalDivider()

                    Column {
                        DevLevelEnum.entries.forEachIndexed { index, it ->
                            Button(
                                onClick = {
                                    viewModel.rememberDevLevel.value = it
                                    devLevelSelectIdx = index
                                },
                                colors = ButtonColors(
                                    containerColor = if (index == devLevelSelectIdx) {
                                        Color(37,137,143)
                                    } else {
                                        Color(107,197,193)
                                    },
                                    contentColor = Color.White,
                                    disabledContainerColor = MaterialTheme.colorScheme.outline,
                                    disabledContentColor = Color.White
                                )
                            ) {
                                Text(it.text)
                            }

                        }

                    }


                }
            }

        }
    }
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


