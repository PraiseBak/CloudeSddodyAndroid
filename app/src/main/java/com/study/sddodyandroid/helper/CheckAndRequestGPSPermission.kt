package com.study.sddodyandroid.helper

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.FusedLocationProviderClient

@Composable
fun CheckAndRequestGPSPermission(fusedLocationClient: FusedLocationProviderClient) {
    val context = LocalContext.current

    /** 요청할 권한 **/
    val permissions = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    val launcherMultiplePermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->

        val areGranted = if (permissionsMap.isNotEmpty()) {
            permissionsMap.values.reduce { acc, next -> acc && next }
        } else {
            false
        }
        /** 권한 요청시 동의 했을 경우 **/
        /** 권한 요청시 동의 했을 경우 **/
        if (areGranted) {
            Log.d("test5", "권한이 동의되었습니다.")
        }
        /** 권한 요청시 거부 했을 경우 **/
        /** 권한 요청시 거부 했을 경우 **/
        else {
            Log.d("test5", "권한이 거부되었습니다.")
        }
    }

    checkAndRequestPermissions(
        context,
        permissions,
        launcherMultiplePermissions,
        fusedLocationClient
    )

}

@Composable
fun checkAndRequestPermissions(
    context: Context,
    permissions: Array<String>,
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
    fusedLocationClient: FusedLocationProviderClient,
) {

    /** 권한이 이미 있는 경우 **/
    if (permissions.all {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }) {
        Log.d("test5", "권한이 이미 존재합니다.")
    }

    /** 권한이 없는 경우 **/
    else {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isLocationEnabled) {
            Toast.makeText(context, "위치 기능이 꺼져있습니다. 위치 기능을 켜주세요", Toast.LENGTH_SHORT).show()
        }

        if (!isAllowPermission(context, permissions)) {
            Toast.makeText(context, "위치 권한이 거부되었습니다. 앱 설정에서 위치 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
        } else {
            SideEffect {
                launcher.launch(permissions)
                Log.d("test5", "권한을 요청하였습니다.")
            }
        }
    }


}

fun isAllowPermission(
    context: Context,
    permissions: Array<String>
): Boolean {
    var isAllowPermission = false
    permissions.forEach {
        if(ContextCompat.checkSelfPermission(context,it) != -1){
            isAllowPermission = true
        }
    }

    return isAllowPermission
}