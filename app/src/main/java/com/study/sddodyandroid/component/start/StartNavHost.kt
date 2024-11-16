package com.study.sddodyandroid.component.start

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.study.sddodyandroid.activity.MainActivity
import com.study.sddodyandroid.common.CheckedEnumViewModel
import com.study.sddodyandroid.component.start.Destinations.MAIN
import com.study.sddodyandroid.component.start.Destinations.MEMBER_INFO_ROUTE
import com.study.sddodyandroid.component.start.Destinations.WELCOME_ROUTE

object Destinations{
    const val WELCOME_ROUTE = "welcome"
    const val MEMBER_INFO_ROUTE = "member"
    const val MAIN = "main"
}




@Composable
fun StartNavHost(
    navController: NavHostController = rememberNavController(),
    context: Context,
    viewModel: CheckedEnumViewModel,
    fusedLocationProviderClient: FusedLocationProviderClient,
    isModifyUser : Boolean = false
) {
    val sharedPreferences = context.getSharedPreferences("KakaoLoginPreferences", Context.MODE_PRIVATE)
    val isLogin = sharedPreferences.getString("accessToken",null) != null
    val memberId = sharedPreferences.getString("memberId",null)
    val isDeletedMember = sharedPreferences.getBoolean("isDeleted",false)
    var startDestination = if (isLogin) MEMBER_INFO_ROUTE else WELCOME_ROUTE
    startDestination = if (isLogin && memberId != null) MAIN else startDestination
    //유저수정인 경우
    if(isModifyUser){
        startDestination = MEMBER_INFO_ROUTE
    }
    if(isDeletedMember) startDestination = WELCOME_ROUTE

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ){
        composable(WELCOME_ROUTE) {
            WelcomeRoute(
                sharedPreferences,
                navController
            )
        }

        composable(MAIN){
            if(!isDeletedMember){
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }

            navController.navigate(WELCOME_ROUTE)

        }

        composable(MEMBER_INFO_ROUTE) {
            MemberRoute(
                memberId = memberId,
                isModifyUser = isModifyUser,
                type = "language",
                navController = navController,
                viewModel = viewModel,
                onNavUp = navController::navigateUp,
                fusedLocationProviderClient = fusedLocationProviderClient
            )
        }

        composable("developer") {
            MemberRoute(
                type = "developer",
                navController = navController,
                viewModel = viewModel,
                onNavUp = navController::navigateUp,
                fusedLocationProviderClient = fusedLocationProviderClient
            )
        }

        composable("framework") {
            MemberRoute(
                type = "framework",
                navController = navController,
                viewModel = viewModel,
                onNavUp = navController::navigateUp,
                fusedLocationProviderClient = fusedLocationProviderClient
            )
        }

        composable("introduce") {
            MemberRoute(
                type = "introduce",
                navController = navController,
                viewModel = viewModel,
                onNavUp = navController::navigateUp,
                fusedLocationProviderClient = fusedLocationProviderClient
            )
        }

        composable("location"){
            MemberRoute(
                type = "location",
                navController = navController,
                viewModel = viewModel,
                onNavUp = navController::navigateUp,
                fusedLocationProviderClient = fusedLocationProviderClient
            )
        }

    }

}

