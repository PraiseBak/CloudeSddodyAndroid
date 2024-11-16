package com.study.sddodyandroid.ui.component.study

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberDrawerState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.R
import com.study.sddodyandroid.common.StudyAdminViewModel
import com.study.sddodyandroid.dto.StudyResponseDto
import com.study.sddodyandroid.helper.study.StudyStateEnum
import com.study.sddodyandroid.service.updateStudyStatus
import com.study.sddodyandroid.ui.common.AppBarColor
import com.study.sddodyandroid.ui.component.ManagePeopleDrawer


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ReplyDockedSearchBar(
//    emails: List<PostDto>,
//    onSearchItemSelected: (PostDto) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var query by remember { mutableStateOf("") }
//    var active by remember { mutableStateOf(false) }
//    val searchResults = remember { mutableStateListOf<PostDto>() }
//
//    LaunchedEffect(query) {
//        searchResults.clear()
//        if (query.isNotEmpty()) {
//            searchResults.addAll(
//                emails.filter {
//                    it.subject.startsWith(
//                        prefix = query,
//                        ignoreCase = true
//                    ) || it.sender.fullName.startsWith(
//                        prefix =
//                        query,
//                        ignoreCase = true
//                    )
//                }
//            )
//        }
//    }
//
//    DockedSearchBar(
//        modifier = modifier,
//        query = query,
//        onQueryChange = {
//            query = it
//        },
//        onSearch = { active = false },
//        active = active,
//        onActiveChange = {
//            active = it
//        },
//        placeholder = { Text(text = stringResource(id = R.string.search_emails)) },
//        leadingIcon = {
//            if (active) {
//                Icon(
//                    imageVector = Icons.Default.ArrowBack,
//                    contentDescription = stringResource(id = R.string.back_button),
//                    modifier = Modifier
//                        .padding(start = 16.dp)
//                        .clickable {
//                            active = false
//                            query = ""
//                        },
//                )
//            } else {
//                Icon(
//                    imageVector = Icons.Default.Search,
//                    contentDescription = stringResource(id = R.string.search),
//                    modifier = Modifier.padding(start = 16.dp),
//                )
//            }
//        },
//        trailingIcon = {
//            ReplyProfileImage(
//                drawableResource = R.drawable.avatar_6,
//                description = stringResource(id = R.string.profile),
//                modifier = Modifier
//                    .padding(12.dp)
//                    .size(32.dp)
//            )
//        },
//    ) {
//        if (searchResults.isNotEmpty()) {
//            LazyColumn(
//                modifier = Modifier.fillMaxWidth(),
//                contentPadding = PaddingValues(16.dp),
//                verticalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//                items(items = searchResults, key = { it.id }) { email ->
//                    ListItem(
//                        headlineContent = { Text(email.subject) },
//                        supportingContent = { Text(email.sender.fullName) },
//                        leadingContent = {
//                            ReplyProfileImage(
//                                drawableResource = email.sender.avatar,
//                                description = stringResource(id = R.string.profile),
//                                modifier = Modifier
//                                    .size(32.dp)
//                            )
//                        },
//                        modifier = Modifier.clickable {
//                            onSearchItemSelected.invoke(email)
//                            query = ""
//                            active = false
//                        }
//                    )
//                }
//            }
//        } else if (query.isNotEmpty()) {
//            Text(
//                text = stringResource(id = R.string.no_item_found),
//                modifier = Modifier.padding(16.dp)
//            )
//        } else
//            Text(
//                text = stringResource(id = R.string.no_search_history),
//                modifier = Modifier.padding(16.dp)
//            )
//    }
//}
@Composable
fun StudyStatusConfigDropdown(
    onStatusChangeClick: (StudyStateEnum) -> Unit,
    navController: NavHostController,
    expanded: MutableState<Boolean>,
    studyAdminViewModel: StudyAdminViewModel,
    rememberStudy: MutableState<StudyResponseDto?>? = null
) {
    val curStatus = rememberStudy?.value?.studyStateEnum
    if(curStatus == StudyStateEnum.END || curStatus == null){
        return
    }

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        var isValid = true
        //준비단계일때만 스터디 시작 가능
        if(rememberStudy?.value?.studyStateEnum != StudyStateEnum.PREPARATION) isValid = false

        val startText = "스터디 시작(더이상 참여 불가)"
        val prepText = "스터디 준비"
        val endText = "스터디 종료(다시 되돌릴 수 없어요)"

        if(curStatus != StudyStateEnum.STUDYING){
            DropdownMenuItem(
                text = {Text(startText)},
                onClick = {
                    expanded.value = false
                    onStatusChangeClick(StudyStateEnum.STUDYING)
                },
            )
        }

        if(curStatus != StudyStateEnum.PREPARATION){
            DropdownMenuItem(
                text = {Text(prepText)},
                onClick = {
                    expanded.value = false
                    onStatusChangeClick(StudyStateEnum.PREPARATION)
                },
            )
        }

        DropdownMenuItem(
            text = {Text(endText)},
            onClick = {
                expanded.value = false
                onStatusChangeClick(StudyStateEnum.END)
            },
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SddodyTopAppBar(
    text: String = "",
    isFullScreen: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    studyAdminViewModel: StudyAdminViewModel,
    rememberStudy: MutableState<StudyResponseDto?>? = null,
    isAdmin : Boolean = false,
) {
    var context = LocalContext.current
    var drawerOpen = remember{ mutableStateOf(false) }
    var drawerState = rememberDrawerState(androidx.compose.material.DrawerValue.Closed)
    var expanded = remember { mutableStateOf(false) }


    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = AppBarColor()
        ),
        title = {
            Column(
                modifier = Modifier.padding(top = 0.dp),
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
//                    modifier = Modifier.padding(8.dp),
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
            if(isAdmin){
                IconButton(
                    onClick = {
                        drawerOpen.value = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "스터디 인원 관리",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = {
                        expanded.value = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "스터디 상태 관리",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StudyStatusConfigDropdown(
                    onStatusChangeClick = {newStatus ->
                        val studyDto = studyAdminViewModel.getStudyDto()
                        studyDto?.let {
                            updateStudyStatus(it.studyId, newStatus,navController,context)
                        }
                    },
                    navController = navController,expanded,
                    studyAdminViewModel = studyAdminViewModel,
                    rememberStudy = rememberStudy
                )
            }


        }
    )




    LaunchedEffect(drawerOpen.value){
        if(drawerOpen.value){
            drawerState.open()
            drawerOpen.value = false
        }
    }

    if(drawerState.isOpen){
        ManagePeopleDrawer(drawerState= drawerState,studyAdminViewModel,navController)
    }

}