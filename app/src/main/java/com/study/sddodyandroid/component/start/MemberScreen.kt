import com.study.sddodyandroid.component.start.SetDevIntroduce


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.location.FusedLocationProviderClient
import com.study.sddodyandroid.R
import com.study.sddodyandroid.common.CheckedEnumViewModel
import com.study.sddodyandroid.component.start.SetLocation
import com.study.sddodyandroid.service.sendSetMemberInfoRequest
import com.study.sddodyandroid.ui.component.start.SetEnumRowItem
import com.study.sddodyandroid.ui.theme.slightlyDeemphasizedAlpha
import com.study.sddodyandroid.ui.theme.stronglyDeemphasizedAlpha
import com.study.sddodyandroid.ui.utils.supportWideScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MemberScreen(
    isModifyUser : Boolean = false,
    type: String,
    navController: NavHostController,
    viewModel: CheckedEnumViewModel,
    onClosePressed: () -> Unit,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
    onDonePressed: () -> Unit,
    fusedLocationProviderClient: FusedLocationProviderClient,
    ){


    var actualClosePressed : () -> Unit = if(isModifyUser) { { run { navController.navigate("main") } } } else onClosePressed
    val context = LocalContext.current
    Surface(modifier = Modifier.supportWideScreen()) {
        Scaffold(
            topBar = {
                if(!type.equals("location")){
                    SurveyTopAppBar(
                        type = type,
                        onClosePressed = actualClosePressed
                    )
                }
            },
            content = {innerPadding->
                Box(modifier = Modifier.padding(innerPadding)){
                    val modifier = Modifier.padding(innerPadding)
                    if(type.equals("location")){
                        Log.d("location", "${fusedLocationProviderClient != null}")
                        if (fusedLocationProviderClient != null) {
                            SetLocation(
                                navController = navController,
                                viewModel = viewModel,
                                fusedLocationProviderClient,
                                isSignup = true
                            )
                        }
                    }
                    else if(type.equals("introduce")){
                        SetDevIntroduce(navController = navController, viewModel = viewModel)
                    }
                    else {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            //3가지 경우 생각해야함 language, developer, stack
                            Text(
                                text = stringResource(id = setTitleResource(type)),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha),
                                modifier = modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.inverseOnSurface,
                                        shape = MaterialTheme.shapes.large
                                    )
                                    .padding(vertical = 24.dp, horizontal = 16.dp)
                            )
                            SetEnumRowItem(titleResourceId= setTitleResource(type)
                                ,type, viewModel)
                        }
                    }
                }
            },
            bottomBar = {
                SurveyBottomBar(
                    context = context,
                    type = type,
                    navController = navController,
                    curType = type,
                    nextType = setNextType(type),
                    viewModel = viewModel,
                    onPreviousPressed = onPreviousPressed,
                    onNextPressed = onNextPressed,
                    onDonePressed = onDonePressed
                )
            }
        )
    }
}

fun setTitleResource(type: String): Int {
    if(type.equals("language")) return R.string.language
    if(type.equals("developer")) return R.string.developer
    if(type.equals("framework")) return R.string.framework
    if(type.equals("introduce")) return R.string.introduce
    return 0
}
fun setNextType(type:String):String{
    if(type.equals("language")) return "developer"
    if(type.equals("developer")) return "framework"
    if(type.equals("framework")) return "introduce"
    if(type.equals("introduce")) return "location"
    return ""
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyTopAppBar(
    type: String,
    onClosePressed: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.membership),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            },
            actions = {
                IconButton(
                    onClick = onClosePressed,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = stringResource(id = R.string.close),
                        tint = MaterialTheme.colorScheme.onSurface.copy(stronglyDeemphasizedAlpha)
                    )
                }
            }
        )
    }
}

@Composable
fun SurveyBottomBar(
    context: Context,
    type: String,
    navController: NavHostController,
    curType: String,
    nextType:String,
    viewModel: CheckedEnumViewModel,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
    onDonePressed: () -> Unit
) {
    val list = viewModel.setTypeToList(curType)
    Surface(shadowElevation = 7.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Since we're not using a Material component but we implement our own bottom bar,
                // we will also need to implement our own edge-to-edge support. Similar to the
                // NavigationBar, we add the horizontal and bottom padding if it hasn't been consumed yet.
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
//                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            if (true) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Text(text = stringResource(id = R.string.previous))
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            if (type.equals("location")) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(47, 147, 143)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = {
                        val memberDto = viewModel.getMemberDto()
                        sendSetMemberInfoRequest(
                            memberDto,
                            navController,
                            context,
                            viewModel.profileImg
                        )
                    },
//                    enabled = isNextButtonEnabled,
                ) {
                    Text(text = stringResource(id = R.string.done))
                }
            }else if(type.equals("introduce")){
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =  Color(47,147,143)
                    ),
                    onClick = {
                        //navController.navigate(nextType)
                        if(viewModel.nickname.value.length !in 2..20){
                            Toast.makeText(context, "닉네임은 2글자이상 20글자이하여야 합니다 ", Toast.LENGTH_LONG).show()
                        }
                        else if(viewModel.selfIntroduce.value.length !in 2..200){
                            Toast.makeText(context, "자기소개는 2글자이상 200글자이하여야 합니다 ", Toast.LENGTH_LONG).show()
                        }
                        else{
                            navController.navigate(nextType)
                        }
                    },
                ) {
                    Text(text = "다음")
                }
            }
            else {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =  Color(47,147,143)
                    ),
                    onClick = {
                        if(list.size == 0){
                            Toast.makeText(context, "버튼을 1개 이상 선택해 주세 요", Toast.LENGTH_SHORT).show()
                        }else{
                            Log.d("welcome", nextType)
                            navController.navigate(nextType)
                        }
                    },
                ) {
                    Text(text = "다음")
                }
            }
        }
    }
}



