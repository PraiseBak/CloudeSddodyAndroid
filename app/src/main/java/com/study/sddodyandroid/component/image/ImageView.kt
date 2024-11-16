package com.study.sddodyandroid.component.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest

@Composable
fun SvgImage(filePath: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(filePath)
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        contentDescription = null
    )
}


@Composable
fun GithubGrassImage(nickname : String) {
    val imageUri = "https://ghchart.rshah.org/${nickname}"
    SvgImage(filePath = imageUri)
}



