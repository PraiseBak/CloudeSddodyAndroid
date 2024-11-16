package com.study.sddodyandroid.ui.component.start

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.study.sddodyandroid.helper.CheckedEnum

@Composable
fun MembershipRowItemButton(
        text: String,
        isSelected: Boolean = false,
        onSelected: () -> Unit,
        onDeselected: () -> Unit,
        CheckedEnum: CheckedEnum,
        clickCount: MutableState<Int>,
        isButtonDisabled: Boolean,
        modifier: Modifier
) {
    val rememberedIsSelected = remember { mutableStateOf(isSelected) }
    val context = LocalContext.current
//                            Color(0x007FD9D5)
//                        Color(0x00EFF4E7)
//                        color = Color(0xFFEFF4E7)
    val buttonColor = if (rememberedIsSelected.value) {
        Color(47,147,143)
    } else {
        Color(107,197,193)
    }
    Button(
//        shape = MaterialTheme.shapes.medium,
        onClick = {
            if(!isButtonDisabled){
                if (!rememberedIsSelected.value) onSelected()
                else onDeselected()
                rememberedIsSelected.value = !rememberedIsSelected.value
                CheckedEnum.isChecked = !CheckedEnum.isChecked
            }
            else if(isButtonDisabled && rememberedIsSelected.value){
                onDeselected()
                rememberedIsSelected.value = !rememberedIsSelected.value
                CheckedEnum.isChecked = !CheckedEnum.isChecked
            }
            else{
                Toast.makeText(context, "버튼은 3개까지만 눌러주세요", Toast.LENGTH_SHORT).show()
            }
        },
        modifier = modifier,
        shape = MaterialTheme.shapes.medium, // 여기서 모서리를 둥글게 지정
        contentPadding = PaddingValues(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        ),
    ) {
        Text(
            text = "$text",
            maxLines = 1,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        )
    }

}

