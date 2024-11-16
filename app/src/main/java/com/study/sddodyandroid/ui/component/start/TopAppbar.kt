package com.example.test.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppbar(text: String){
    TopAppBar(
        title = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = ListItemDefaults.contentColor
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    // Handle navigation icon click event
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = ListItemDefaults.contentColor
                )
            }
        },
        actions = { /* Action items if needed */ },

        )
}