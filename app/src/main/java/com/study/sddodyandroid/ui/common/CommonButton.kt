package com.study.sddodyandroid.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CommonButton(text:String,modifier: Modifier,onClick: () -> Unit){
    val buttonColor = Color(0xFF356859)
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = MaterialTheme.shapes.medium, // 여기서 모서리를 둥글게 지정
        contentPadding = PaddingValues(20.dp),

        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        )
    ) {
        //아이콘 버튼으로 교체
        Text(
            text = text,
            maxLines = 1,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        )
    }
}
