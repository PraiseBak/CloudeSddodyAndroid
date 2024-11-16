package com.study.sddodyandroid.component.start

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.study.sddodyandroid.dto.LocationDto

@Composable
fun SearchLocationList(
    searchResultList: MutableList<LocationDto>,
    selectLocation: MutableState<LocationDto?>,
    currentCameraPositionState: CameraPositionState,
    onFocus : (Boolean) -> Unit
) {
    val focusManager = LocalFocusManager.current

    if(searchResultList.size == 0){
        Column(modifier = Modifier.padding(top= (80).dp, start = (15).dp, end = (15).dp)) {
            Text(text = "최근 검색된 위치가 없어요.")
        }
    }

    LazyColumn (modifier = Modifier.padding(top= (80).dp)){
        items(searchResultList) { item ->
            ListItem(
                headlineContent = { Text(item.name) },
                supportingContent = { Text(item.address) },
                modifier = Modifier.clickable {
                    val curLocation = LatLng(item.x,item.y)
                    val newPosition = CameraPosition.fromLatLngZoom(curLocation, 16f)
                    val cameraUpdateFactory = CameraUpdateFactory.newCameraPosition(newPosition)
                    currentCameraPositionState.move(cameraUpdateFactory)
                    onFocus(false)
                    focusManager.clearFocus(false)
                },
//
//                headlineContent = { Text("${item.address}") },
                leadingContent = {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = "위치"
                    )

                }
            )
            HorizontalDivider()
        }
    }

}
