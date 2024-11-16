package com.study.sddodyandroid.component.main.createstudy

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.activity.MainViewRoute.CREATE_STUDY
import com.study.sddodyandroid.ui.common.CommonBackgroundColor

@Composable
fun SelectedCreateView(
    navController: NavHostController,modifier: Modifier
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(8.dp)
                .border(1.dp, Color.Black,shape = CardDefaults.shape)
                .clickable { navController.navigate(CREATE_STUDY) },
            colors = CardDefaults.cardColors(
                containerColor = CommonBackgroundColor(),
            )
        ) {
            // 첫 번째 Card의 내용
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                // Text를 수직 및 수평 중앙에 정렬
                Text(
                    text = "스터디 개설",
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // 간격을 위한 Spacer 추가

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(8.dp)
                .border(1.dp, Color.Black,shape = CardDefaults.shape)
                .clickable { navController.navigate("community/add") },
            colors = CardDefaults.cardColors(
                containerColor = CommonBackgroundColor(),
            ),        ) {
            // 두 번째 Card의 내용
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                // Text를 수직 및 수평 중앙에 정렬
                Text(
                    text = "게시글 작성",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}