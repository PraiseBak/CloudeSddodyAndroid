package com.study.sddodyandroid.ui.common

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


/**
 * 연한 청록색계열
 *     val buttonColor = if (rememberedIsSelected.value) {
 *         Color(47,147,143)
 *     } else {
 *         Color(107,197,193)
 *     }
 */


@Composable
fun CommonButtonOnlyColor() : Color{
    val color = Color(android.graphics.Color.parseColor("#FFDDDD"))
    return color
}

@Composable
fun CommonButtonColor (): ButtonColors {
//    val color = Color(android.graphics.Color.parseColor("#EFEFEF"))

//    val color = Color(107,197,193).copy(0.5f)
    val color = Color(android.graphics.Color.parseColor("#FFDDDD"))
//    val color = Color(47,147,143).copy(0.3f)


//    Color(47,147,143)
    return ButtonDefaults.buttonColors(
//        containerColor =  Color(255,219,204 ),
//        containerColor =  Color(162,225,219),
        containerColor =  color,
//        containerColor = Color(127, 217, 213),
//        contentColor = Color(239,244,231)
        contentColor = Color.Black,
//        contentColor = Color(0xFFF4F4F4)
    )

}

@Composable
fun CommonSecondaryButtonColor (): ButtonColors {
    return ButtonDefaults.buttonColors(
        containerColor = Color(236,234,228),
//        contentColor = Color(239/,244,231)
//        contentColor = Color.Black,
        contentColor = Color(0xFFF4F4F4)
    )

}


@Composable
fun CommonCardBackgroundColor() : Color{
//    return Color(255,219,204)
    return Color(97,197,193)

}

@Composable
fun CommonBackgroundColor (): Color {
//    return Color(236,234,228)
//    return Color.White.copy(alpha = 0.9f)
//    return Color.LightGray.copy(alpha = 0.0001f)
//    val color = Color(android.graphics.Color.parseColor("#EFEFEF"))
//    val color = Color(android.graphics.Color.parseColor("#ADE7E7"))
//    val color = Color(android.graphics.Color.parseColor("#FFDDDD")).copy(0.1f)
//

//    return color
    return Color.White
//    return Color(239,244,231).copy(0.5f)
//    return Color.DarkGray.copy(alpha = 0.5f)
}

@Composable
fun CommonDarkBackgroundColor() : Color{
    return Color.Black
}

@Composable
fun TestColor() : Color{
//    val color = Color(android.graphics.Color.parseColor("#FFDDDD"))

//    val color = Color(android.graphics.Color.parseColor("#DAEAF6"))
    return Color(47,147,143)
//    return color
}

@Composable
fun CommonSecondaryBackgroundColor (): Color {
//    .background(color = Color.LightGray.copy(alpha = 0.3f))
//    return Color(255,219,204).copy(0.7f)
//    return CommonBackgroundColor()
    return Color.LightGray.copy(0.3f)
//    return Color.DarkGray.copy(alpha = 0.5f)
}



@Composable
fun AppBarColor() : Color{
//    val color = Color(android.graphics.Color.parseColor("#FFDDDD"))
    val color = Color(android.graphics.Color.parseColor("#FFDDDD"))
    return color
}