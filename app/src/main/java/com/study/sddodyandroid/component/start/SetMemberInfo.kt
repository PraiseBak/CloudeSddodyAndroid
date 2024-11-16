package com.study.sddodyandroid.component.start

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.common.CheckedEnumViewModel
import com.study.sddodyandroid.ui.common.CommonColumn

@Composable
fun SetMemberInfo(navController: NavHostController, viewModel: CheckedEnumViewModel) {
    Log.d("debugg", "SetMemberInfo")
    CommonColumn()
    {
        Text(text = "현재 화면")

        Button(
            onClick = {
                // 다음 화면으로 이동
                viewModel.sharedData = "test"
                navController.navigate("language")
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "다음 화면으로 이동")
        }

        Button(
            onClick = {
                // 뒤로 가기
                navController.popBackStack()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "뒤로 가기")
        }
    }
}

