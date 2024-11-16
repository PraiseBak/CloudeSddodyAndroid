package com.study.sddodyandroid.component.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.study.sddodyandroid.R


@Composable
fun UnknownMemberImage(modifier: Modifier = Modifier, onImageClick: () -> Unit = {}){
    val context = LocalContext.current
    val drawable = context.resources.getDrawable(R.drawable.baseline_account_circle_24, null)
    val bitmap = drawable.toBitmap()

    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clip(CircleShape)
    ) {
        BitMapImage(bitmap = bitmap.asImageBitmap(),
            modifier = modifier
                .padding(horizontal = 4.dp)
                .clip(CircleShape)
                .clickable {
                    onImageClick()
                }
        )
    }
}

@Composable
fun BitMapImage(
    bitmap: ImageBitmap, modifier: Modifier = Modifier
){
    Image(bitmap = bitmap,
        contentDescription = "이미지",
        modifier = modifier
    )
}