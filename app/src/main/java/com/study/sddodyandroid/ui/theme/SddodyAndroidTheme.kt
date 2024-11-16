package com.study.sddodyandroid.ui.theme

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SddodyAndroidTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = typography,
        shapes = shapes,
        content = content
    )
}