package com.study.sddodyandroid.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TitleText(str: String){

    Text(
        text = str,
        modifier = Modifier
            .padding(start = 10.dp,top = 20.dp),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp, // 예시로 24sp로 설정
        textAlign = TextAlign.Center,
        fontStyle = FontStyle.Italic
    )
}