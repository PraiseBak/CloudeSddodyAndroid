package com.study.sddodyandroid.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.R

@Composable
fun CreateFloatingActionButton(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){
    LargeFloatingActionButton(
        onClick = onClick,
        modifier = modifier
            .padding(10.dp),
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = stringResource(id = R.string.edit),
            modifier = Modifier.size(28.dp)
        )
    }
}