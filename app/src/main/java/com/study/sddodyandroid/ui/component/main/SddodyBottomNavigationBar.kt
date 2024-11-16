package com.study.sddodyandroid.ui.component.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.ui.common.AppBarColor
import com.study.sddodyandroid.ui.common.CommonBackgroundColor
import com.study.sddodyandroid.ui.common.CommonCardBackgroundColor

@Composable
fun SddodyBottomNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavigationBar(containerColor = CommonBackgroundColor(),
        modifier = modifier
            .fillMaxWidth()
            .shadow(20.dp)
    ) {
        TOP_LEVEL_DESTINATIONS.forEach { sddodyDestination ->
            NavigationBarItem(
                selected = false,
                onClick = { navController.navigate(sddodyDestination.route) },
                icon = {
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Icon(
                            imageVector = sddodyDestination.selectedIcon,
                            contentDescription = stringResource(id = sddodyDestination.iconTextId)
                        )
                        Text(
                            text = stringResource(id = sddodyDestination.iconTextId),
                        )
                    }
                }
            )
        }
    }
}
