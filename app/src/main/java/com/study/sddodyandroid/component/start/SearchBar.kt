package com.study.sddodyandroid.component.start

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.maps.android.compose.CameraPositionState
import com.study.sddodyandroid.dto.LocationDto

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchTextField(
    onSearch: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    
    Button(onClick = {
        focusManager.clearFocus()
    }) {

    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
            }
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onSearch(searchText)
                },
            )
        )
    }
}



@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    focus: Boolean,
    onFocus: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    searchResultList: SnapshotStateList<LocationDto>,
    selectLocation: MutableState<LocationDto?>,
    cameraPositionState: CameraPositionState,

) {
    var searchFieldText by remember { mutableStateOf(TextFieldValue("")) }

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = searchFieldText,
        onValueChange = {
            searchFieldText = it
        },


        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp)
            .onFocusChanged { onFocus(it.isFocused) }
            .zIndex(1f),


        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = Color.White // 배경색을 흰색으로 지정
        ),

        placeholder = {
            Text("위치 검색")
        },

        leadingIcon = if (focus) {
            {
                IconButton(onClick = {
                    onFocus(false)
                    searchFieldText = TextFieldValue("")
                }) {
                    Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = null)
                }
            }
        } else {
            null
        },


        trailingIcon = {
            IconButton(onClick = {
                onSearch(searchFieldText.text)
            }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            }
        },
    )

    DisposableEffect(focus) {
        if (focus) {
            keyboardController?.show()
        } else {
            keyboardController?.hide()
            focusManager.clearFocus(force = true)
        }
        onDispose { }
    }

    if (focus) {
        SearchLocationList(searchResultList, selectLocation, cameraPositionState,onFocus)
    }

}
