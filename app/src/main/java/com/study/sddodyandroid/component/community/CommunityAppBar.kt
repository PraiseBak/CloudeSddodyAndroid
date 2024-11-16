package com.study.sddodyandroid.component.community

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.R
import com.study.sddodyandroid.ui.common.AppBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityListAppBar(
    text: String,
    isFullScreen: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = AppBarColor()
        ),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = if (isFullScreen) Alignment.Start
                else Alignment.Start
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        navigationIcon = {
            if (isFullScreen) {
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.padding(8.dp),
//                    colors = IconButtonDefaults.filledIconButtonColors(
//                        containerColor = MaterialTheme.colorScheme.surface,
//                        contentColor = MaterialTheme.colorScheme.onSurface
//                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = { /*TODO*/ },
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "포트폴리오 작성",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}
