package com.study.sddodyandroid.component.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DrawerValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.rememberDrawerState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavHostController
import com.study.sddodyandroid.R
import com.study.sddodyandroid.activity.MainViewRoute
import com.study.sddodyandroid.dto.AlarmDto
import com.study.sddodyandroid.service.setAlarmDto
import com.study.sddodyandroid.service.setChatList
import com.study.sddodyandroid.ui.common.AppBarColor
import com.study.sddodyandroid.ui.common.TestColor
import com.study.sddodyandroid.ui.component.AlarmDrawer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar(
    text: String,
    isFullScreen: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    var drawerOpen = remember{ mutableStateOf(false) }
    var drawerState = rememberDrawerState(DrawerValue.Closed)
    var rememberAlarmDto : MutableState<MutableList<AlarmDto>?> = remember{ mutableStateOf(null) }
    val context = LocalContext.current
    var alarmRead = remember{ mutableStateOf(false) }

    if(rememberAlarmDto?.value == null){
        setAlarmDto(context,rememberAlarmDto)
    }

    val updateUIReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // UI 업데이트 로직
            context?.let {
                println("main ui update")
                alarmRead.value = false
                setAlarmDto(context,rememberAlarmDto)
            }
        }
    }

    val intentFilter = IntentFilter("MAIN_UI_BROAD_CAST")
    LocalBroadcastManager.getInstance(LocalContext.current).registerReceiver(updateUIReceiver, intentFilter)

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
                    color = TestColor().copy(1f)
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

            IconButton(
                onClick = { navController.navigate("community/add") },
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "글쓰기",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            BadgedBox(
                badge = {
                    Badge {
                        var badgeNumber = if(alarmRead.value) 0 else {rememberAlarmDto.value?.count{ !(it.isRead) } ?: 0}
                        Text(
                            badgeNumber.toString(),
                            modifier = Modifier.semantics {
                                contentDescription = "$badgeNumber new notifications"
                            }
                        )
                    }
                }) {
                Icon(
                    Icons.Filled.Notifications,
                    contentDescription = "알람",
                    Modifier.clickable{
                        drawerOpen.value = true
                    }
                )
            }
        }
    )

    LaunchedEffect(drawerOpen.value){
        if(drawerOpen.value){
            alarmRead.value = true
            drawerState.open()
            drawerOpen.value = false
        }
    }

    if(drawerState.isOpen && rememberAlarmDto.value != null){
        AlarmDrawer(drawerState = drawerState,navController,rememberAlarmDto.value!!)
    }
}
