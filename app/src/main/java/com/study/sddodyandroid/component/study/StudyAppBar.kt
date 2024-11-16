package com.study.sddodyandroid.component.study

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.R
import com.study.sddodyandroid.activity.MainViewRoute
import com.study.sddodyandroid.helper.study.StudyOrderType
import com.study.sddodyandroid.ui.common.AppBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyAppBar(
    text: String,
    isFullScreen: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onSortClick : (StudyOrderType) -> Unit
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
                onClick = { navController.navigate(MainViewRoute.CREATE_STUDY) },
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = "스터디 생성",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconSortButton(onSortClick = onSortClick ,navController = navController)

        }
    )
}

@Composable
fun IconSortButton(
    onSortClick: (StudyOrderType) -> Unit,
    navController : NavHostController
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(
        onClick = { expanded = true },
    ) {
        Icon(
            imageVector = Icons.Default.Sort,
            contentDescription = "정렬",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        repeat(6){index ->
            val text = when (index) {
                0 -> "날짜순"
                1 -> "내가 관심있는 프레임워크"
                2 -> "위치"
                3 -> "지도"
                4 -> "추천"
                5 -> "종료된 스터디"
                6 -> "진행중인 스터디"
                else -> ""
            }

            DropdownMenuItem(
                text = {Text(text)},
                onClick = {
                    expanded = false
                    val orderType = when (index) {
                        0 -> StudyOrderType.RECENT_CREATE
                        1 -> StudyOrderType.MOST_FIT
                        2 -> StudyOrderType.NEARBY
                        3 -> {
                            navController.navigate("study/map")
                            StudyOrderType.NEARBY // 예시로 넣은 것이므로 적절한 값으로 수정 필요
                        }
                        4 -> StudyOrderType.STATISTIC
                        5 -> StudyOrderType.END
                        6 -> StudyOrderType.STUDYING
                        else -> StudyOrderType.RECENT_CREATE // 예시로 넣은 것이므로 적절한 값으로 수정 필요
                    }
                    onSortClick(orderType)
                },
            )


        }

    }

}

