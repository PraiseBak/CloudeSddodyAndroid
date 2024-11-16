package com.study.sddodyandroid.ui.component.main.study

import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.alpha
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavHostController
import com.study.sddodyandroid.R
import com.study.sddodyandroid.activity.MainViewRoute
import com.study.sddodyandroid.component.conversation.AsyncImageWithHeader
import com.study.sddodyandroid.component.image.BitMapImage
import com.study.sddodyandroid.dto.StudyResponseDto
import com.study.sddodyandroid.service.compareAndFormatDates
import com.study.sddodyandroid.service.formatDate
import com.study.sddodyandroid.ui.common.CommonButtonColor
import java.time.Instant
import java.util.Date



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun StudyCard(
    study: StudyResponseDto,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onClick: (Long) -> Unit,
    isPreview: Boolean = false,

){
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
                colors = CardDefaults.cardColors(
//        containerColor =  Color.LightGray.alpha,
//            containerColor = Color(127, 217, 213),
            containerColor = Color.White,

        ),
        modifier = modifier
            .fillMaxWidth(1f)
            .padding(vertical = 8.dp, horizontal = 16.dp)
//            .clip(CardDefaults.shape)
            .clickable { onClick(study.studyId) }

    ) {
        val studyMemberDtoList = study.studyMemberInfoDtoList
        val context = LocalContext.current

        Column {
            //관리자 프로필
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 8.dp, bottom = 1.dp)
            ) {
                var isEmptyUri = study.studyImageSrcList.isEmpty()
                val imageUri =
                    if (isEmptyUri) "default_img.png" else study.studyImageSrcList.first()
            if (!isPreview) {
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .width(100.dp)
                            .height(110.dp)
                            .clip(shape = RoundedCornerShape(15.dp))
                    ) {
                        if(isEmptyUri){
                            val drawable = context.resources.getDrawable(R.drawable.round_menu_book_24, null)
                            val bitmap = drawable.toBitmap()
                            Column(modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                BitMapImage(bitmap = bitmap.asImageBitmap(),
                                    modifier = Modifier.size(100.dp)

                                )
                            }
                        }else{
                            Box(modifier = Modifier.clickable{
                                navController.navigate("${MainViewRoute.PROFILE}/${study.ownerMemberInfo.id}")
                            }){
                                AsyncImageWithHeader(
                                    uri = imageUri
                                ) { imageUri ->
                                    navController.navigate("image/zoom/$imageUri")
                                }
                            }
                        }

                        if (study.studyImageSrcList.isNotEmpty()) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.Transparent
                                ),

                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 8.dp, bottom = 2.dp)

                            ) {
                                Text(
                                    text = "1/${study.studyImageSrcList.size}",
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }




                // 두 번째 영역
                Box(
                    modifier = Modifier
                        .weight(4f)
//                        .fillMaxHeight()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally){
                        Row(modifier = Modifier

//                            .align(if (isPreview) Alignment.CenterHorizontally else Alignment.Start))
                            .align(Alignment.CenterHorizontally))
                        {
                            if(!isPreview){
                                AsyncImageWithHeader(
                                    uri = study.ownerMemberInfo.memberProfileImgSrc,
                                    isProfile = true,
                                    onProfileClick = {
                                        navController.navigate("PROFILE/${study.ownerMemberInfo.id.toString()}")
                                    }
                                )
                            }
                        }
                        Text(
                            text = study.ownerMemberInfo.nickname,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .padding(top = 1.dp)
                        )


                        Text(
                            text = study.title,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 1.dp),
                            overflow = TextOverflow.Ellipsis
                        )
                        HorizontalDivider()

                        Row(modifier = Modifier
                            .padding(top = 0.dp)
                            ){
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = "인원 수",
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(start = 10.dp)
                            )

                            Text(
                                text = "${studyMemberDtoList.size}/${study.maxStudyMemberNum}",
                                style = MaterialTheme.typography.labelSmall,

                                modifier = Modifier
                                    .padding(start = 4.dp, top = 6.dp)
                            )

                            Icon(
                                imageVector = Icons.Default.Handyman,
                                contentDescription = "현재 상태",
                                modifier = Modifier
                                    .size(28.dp)
                                    .padding(start = 10.dp)
                            )

                            Text(
                                text = study.studyStateEnum.text,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier
                                    .padding(start = 4.dp, top = 6.dp)
                            )


                            Icon(
                                imageVector = Icons.Default.Work,
                                contentDescription = "직종",
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(start = 10.dp)
                            )

                            Text(
                                text = study.devLevel.text,
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
                                text = study.devYear.yearStr,
                                style = MaterialTheme.typography.labelSmall,

                                modifier = Modifier
                                    .padding(start = 4.dp, top = 6.dp)
                            )
                        }

                        Text(
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(end = 12.dp),
                            text = compareAndFormatDates(
                                study.createdAt,
                                Date.from(Instant.now())
                            ) + " " + formatDate(study.createdAt),
                        )

                    }
                }
            }


            LazyRow(modifier =Modifier.padding(bottom = 1.dp,start = 4.dp)){

                items(study.studyTechStack){
                    Button(colors = CommonButtonColor(),
                        onClick={},
                        modifier = Modifier.padding(start = 10.dp)) {
                        Text(text = it.info)
                    }
                }
            }


        }

    }
}

