package com.study.sddodyandroid.ui.component.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.study.sddodyandroid.R
import com.study.sddodyandroid.activity.MainViewRoute


data class MainViewDestination(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int
)

val TOP_LEVEL_DESTINATIONS = listOf(
    MainViewDestination(
        route = MainViewRoute.MAIN,
        selectedIcon = Icons.Default.Home,
        unselectedIcon = Icons.Default.Home,
        iconTextId = R.string.HOME
    ),
    MainViewDestination(
        route = MainViewRoute.STUDY_LIST,
        selectedIcon = Icons.Default.MenuBook,
        unselectedIcon = Icons.Default.MenuBook,
        iconTextId = R.string.STUDY
    ),
    MainViewDestination(
        route = MainViewRoute.COMMUNITY_LIST,
        selectedIcon = Icons.Default.Article,
        unselectedIcon = Icons.Default.Article,
        iconTextId = R.string.COMMUNITY
    ),
    MainViewDestination(
        route = "${MainViewRoute.PROFILE}/0",
        selectedIcon = Icons.Default.Person,
        unselectedIcon = Icons.Default.Person,
        iconTextId = R.string.PROFILE
    ),
    MainViewDestination(
        route = "chatroom/list",
        selectedIcon = Icons.Default.Chat,
        unselectedIcon = Icons.Default.Person,
        iconTextId = R.string.CHATROOM_LIST
    )


)
