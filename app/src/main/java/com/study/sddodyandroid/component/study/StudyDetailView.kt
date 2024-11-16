package com.study.sddodyandroid.component.study

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.widget.CalendarView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.study.sddodyandroid.R
import com.study.sddodyandroid.component.chatroom.LoadingIndicator
import com.study.sddodyandroid.component.conversation.AsyncImageWithHeader
import com.study.sddodyandroid.component.image.BitMapImage
import com.study.sddodyandroid.component.start.GoogleMapViewBySpecificLocation
import com.study.sddodyandroid.dto.StudyResponseDto
import com.study.sddodyandroid.helper.study.StudyStateEnum
import com.study.sddodyandroid.service.compareAndFormatDates
import com.study.sddodyandroid.service.formatDate
import com.study.sddodyandroid.service.getStudy
import com.study.sddodyandroid.ui.common.CommonButtonColor
import com.study.sddodyandroid.ui.common.CreateFloatingActionButton
import com.study.sddodyandroid.ui.component.main.profile.PartOfFeedContent
import com.study.sddodyandroid.ui.component.study.StudyCommunity
import kotlinx.coroutines.launch
import me.onebone.toolbar.ExperimentalToolbarApi
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.max


@OptIn(ExperimentalToolbarApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun StudyDetailView(
    studyId: Long,
    navController: NavHostController,
    rememberStudy: MutableState<StudyResponseDto?>,
){
    val context = LocalContext.current
    val state = rememberCollapsingToolbarScaffoldState()
    val rememberLoading =  remember{ mutableStateOf(false)}
    var imageUriList = mutableListOf<Uri>()

    getStudy(id=studyId, context, rememberStudy, rememberLoading)

    Box(modifier = Modifier.fillMaxSize()){


        LazyColumn(
            modifier = Modifier
//                .padding(20.dp)
        ) {



            rememberStudy.value?.let {
                item {
                    LazyRow(
                        content = {
                            val imageSize = it.studyImageSrcList.size

                            items(count = max(1, it.studyImageSrcList.size)) { index ->
                                if (imageSize == 0) {
                                    AsyncImageWithHeader(
                                        uri = "default_img.png",
                                        isStudyDetail = true
                                    ) { imageUri ->
                                        navController.navigate("image/zoom/$imageUri")
                                    }
                                } else {
                                    val imgUri = it.studyImageSrcList[index]
                                    AsyncImageWithHeader(
                                        uri = imgUri,
                                        isStudyDetail = true
                                    ) { imageUri ->
                                        navController.navigate("image/zoom/$imageUri")
                                    }
                                }
                            }
                        }
                    )
                }

                item{
                    val maxWidth = LocalConfiguration.current.screenWidthDp.dp // 화면의 최대 너비를 가져옵니다.

                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.
                            fillMaxSize()
                    ) {
                        Card(modifier = Modifier
                            .padding(20.dp)
                            .widthIn(max = 1f * maxWidth)

                            .offset(y = (-60).dp)
//                            .width(200.dp) // 부모 요소의 너비의 80%로 제한합니다.
                            .fillMaxSize()
                            .background(
                                color = Color.White.copy(alpha = 0.9f),
                                RoundedCornerShape(8.dp)
                            )

                            ,

                            colors = CardDefaults.cardColors(
                                Color.LightGray.copy(alpha = 0.3f)
                            )
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .width(300.dp)
                                    .height(200.dp)
                                ,
                                verticalArrangement = Arrangement.Center, // 수직 방향 중앙 정렬
                                horizontalAlignment = Alignment.CenterHorizontally // 수평 방향 중앙 정렬
                            ) {
                                AsyncImageWithHeader(
                                    uri = it.ownerMemberInfo.memberProfileImgSrc,
                                    isStudyDetail = true,
                                ) { imageUri ->
                                    navController.navigate("image/zoom/$imageUri")
                                }

                                Text(
                                    it.ownerMemberInfo.nickname,
                                    style = MaterialTheme.typography.bodyMedium
                                )


                                Text(
                                    it.title,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                HorizontalDivider()

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = compareAndFormatDates(it.createdAt,
                                        Date.from(Instant.now())) + " " + formatDate(it.createdAt),
                                        style = MaterialTheme.typography.labelSmall

                                    )
                                }

                                Row() {
                                    Icon(
                                        imageVector = Icons.Default.Group,
                                        contentDescription = "인원 수",
                                        modifier = Modifier
                                            .size(28.dp)
                                            .padding(start = 10.dp)
                                    )

                                    Text(
                                        text = "${it.studyMemberInfoDtoList.size}/${it.maxStudyMemberNum}",
                                        modifier = Modifier
                                            .padding(start = 4.dp, top = 6.dp)
                                    )


                                    Icon(
                                        imageVector = Icons.Default.Work,
                                        contentDescription = "직종",
                                        modifier = Modifier
                                            .size(28.dp)
                                            .padding(start = 10.dp)
                                    )

                                    Text(
                                        text = it.devLevel.text,
                                        modifier = Modifier
                                            .padding(start = 4.dp, top = 6.dp)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = "개발 공부 기간",
                                        modifier = Modifier
                                            .size(28.dp)
                                            .padding(start = 10.dp)
                                    )

                                    Text(
                                        text = it.devYear.yearStr,
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
                                        text = it.studyStateEnum.text,
                                        modifier = Modifier
                                            .padding(start = 4.dp, top = 6.dp)
                                    )


                                }
                                LazyRow(){
                                    items(rememberStudy.value!!.studyTechStack){
                                        Button(colors = CommonButtonColor(),
                                            onClick={},
                                            modifier = Modifier.padding(start = 4.dp)) {
                                            Text(text = it.info)
                                        }
                                    }
                                }



                            }


                        }
                    }




                    val study = rememberStudy.value!!

                    Column(
                        modifier = Modifier
                            .offset(y = (0).dp)
                            .padding(20.dp)
                    ) {
                        Text(
                            it.content,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                item{
                    StudyCalendarView();
                }


                item{
                    Column(modifier = Modifier
                        .padding(20.dp)
                        .fillMaxSize()
                        .height(300.dp)){
                        Text(text = "스터디는 다음 장소에서 진행되요!")
                        GoogleMapViewBySpecificLocation(it.location)
                    }
                }

                item{
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = "참여중인 멤버",
                            style = MaterialTheme.typography.headlineMedium
                        )

                        var maxSize = it.studyMemberInfoDtoList.size
                        val rememberViewMore by remember{mutableStateOf(false) }
                        if(!rememberViewMore){
                            maxSize = Math.min(10,it.studyMemberInfoDtoList.size)
                        }
                        //현재 참여하고있는 유저
                        it.studyMemberInfoDtoList.subList(0,maxSize).forEach{
                            Column {
                                Row(){
                                    AsyncImageWithHeader(
                                        uri = it.memberProfileImgSrc,
                                        isProfile = true
                                    )
                                    Column(modifier = Modifier.padding(start =8.dp,top = 4.dp)) {
                                        Text(text = it.nickname,modifier = Modifier.padding())
                                        Text(text = it.selfIntroduce, overflow = TextOverflow.Ellipsis)
                                    }
                                }

                            }
                        }

                        if(it.studyMemberInfoDtoList.size > 10){
                            Button(onClick = {rememberViewMore != rememberViewMore}) {
                                if(rememberViewMore){
                                    Icon(Icons.Default.ExpandLess, contentDescription = "접기")
                                }else{
                                    Icon(Icons.Default.ExpandMore, contentDescription = "펼치기")
                                }
                            }
                        }

                    }
                }

                //스터디 후기
                item{
                    if(rememberStudy.value?.studyStateEnum == StudyStateEnum.END){
                        Text(text = "스터디 후기",
                            style = MaterialTheme.typography.headlineMedium
                        )

                        //스터디 후기
                        PartOfFeedContent(navController = navController,memberId = 0L,isStudyReview = true,studyId)
                    }
                }




            } ?: item {
                LoadingIndicator()
            }

        }
    }
}









fun px2dp1(px: Int, context: Context) = px / ((context.resources.displayMetrics.densityDpi.toFloat()) / DisplayMetrics.DENSITY_DEFAULT)
