package com.study.sddodyandroid.component.start

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.study.sddodyandroid.R
import com.study.sddodyandroid.activity.StartActivity
import com.study.sddodyandroid.service.changeMemberInfo
import com.study.sddodyandroid.ui.common.CommonButtonColor
import com.study.sddodyandroid.ui.common.CommonSecondaryBackgroundColor
import com.study.sddodyandroid.ui.utils.supportWideScreen

@Composable
fun WelcomeRoute(
    sharedPreferences: SharedPreferences,
    navController: NavHostController
){
    WelcomeScreen(sharedPreferences = sharedPreferences, navController = navController)
}

@Composable
fun WelcomeScreen(
    sharedPreferences: SharedPreferences,
    navController: NavHostController
) {
    val isDeleted = sharedPreferences.getBoolean("isDeleted",false)

    Scaffold(modifier = Modifier.supportWideScreen()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = 20.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            val context = LocalContext.current

            Card(colors = CardDefaults.cardColors(
                containerColor = Color.Companion.White),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .heightIn(min = 80.dp)) {
                Branding()

                if(isDeleted){
                    ChangeMemberStatus(context,navController)
                }else{
                    KakaoLoginView(
                        context,
                        onLogin = {},
                        navController,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChangeMemberStatus(context: Context, navController: NavHostController) {
    val sharedPreferences = context.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE)

    Column(modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            Icon(Icons.Default.Info,contentDescription = "경고")
            Text(text = "현재 휴면상태인 계정입니다")
        }
        Button(
            colors = CommonButtonColor(),
            onClick = { changeMemberInfo(context,navController
                ,onEnd = {
                    sharedPreferences.edit().putBoolean("isDeleted",false).apply()
                    val intent = Intent(context, StartActivity::class.java)
                    context.startActivity(intent)

                }
            ) }
        ){
            Text(text = "휴면상태 해지")
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun Branding(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.wrapContentHeight(align = Alignment.CenterVertically)
    ) {

        Logo(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 80.dp)
        )
        Text(
            text = stringResource(id = R.string.app_tagline),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
        )

    }
}

@Composable
private fun Logo(
    modifier: Modifier = Modifier,
    lightTheme: Boolean = LocalContentColor.current.luminance() < 0.5f,
) {
    val assetId = R.drawable.round_book_icon

    Image(
        painter = painterResource(id = assetId),
        modifier = modifier
            .size(200.dp),
        contentDescription = null
    )
}
