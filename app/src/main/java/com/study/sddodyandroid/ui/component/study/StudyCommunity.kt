package com.study.sddodyandroid.ui.component.study

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.component.community.CommunityView
import com.study.sddodyandroid.dto.StudyResponseDto

@Composable
fun StudyCommunity(
    modifier: Modifier,
    navController: NavHostController,
    study: StudyResponseDto
){
    val textModifier = Modifier.padding(start = 10.dp,top =20.dp)

    LazyColumn{
        item{
            Text(
                text = "질문해요",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp, // 예시로 24sp로 설정
                color = Color.Blue,
                modifier = textModifier
            )

            Row (
            ){
                Text(
                    text = "스터디 게시판",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp, // 예시로 24sp로 설정
                    modifier = textModifier
                )

                Text(
                    text = "더보기>>",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp, // 예시로 24sp로 설정
                    color = Color.LightGray,
                    modifier = textModifier.clickable {
                        navController.navigate("study/community/${study.studyId}")
                        //전부 페이지 나오게
                    }
                )
            }

            TabRowDefaults.Divider(
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(start = 10.dp, end = 10.dp)
            )
        }
    }


    CommunityView(
        navController = navController,
        isPreview = true,
        modifier = Modifier,
        studyId = study.studyId,
        onChangeCommunityViewClick ={navController.navigate("community/list")}
    )
}