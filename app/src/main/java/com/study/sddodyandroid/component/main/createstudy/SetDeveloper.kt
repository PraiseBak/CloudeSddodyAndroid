package com.study.sddodyandroid.component.main.createstudy

import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.study.sddodyandroid.common.CreateStudyViewModel
import com.study.sddodyandroid.dto.LocationDto
import com.study.sddodyandroid.helper.DeveloperEnum
import com.study.sddodyandroid.ui.common.CommonBackgroundColor
import com.study.sddodyandroid.ui.common.CommonButtonColor
import com.study.sddodyandroid.ui.common.CommonSecondaryButtonColor
import com.study.sddodyandroid.ui.component.main.create.QuestionWrapper

@Composable
fun SetStudyDeveloper(
    @StringRes titleResourceId: Int,
    developerEnumList: List<DeveloperEnum>,
    viewModel: CreateStudyViewModel,
    onDevSelected: (selected: Boolean, answer: DeveloperEnum) -> Unit,
    modifier: Modifier = Modifier,
){
    QuestionWrapper(
        titleResourceId = titleResourceId,
        modifier = modifier,
    ){
        var selected by remember { mutableStateOf(false) }
        Surface(
            shape = MaterialTheme.shapes.small,
            color = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
            border = BorderStroke(
                width = 1.dp,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                }
            ),
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clip(MaterialTheme.shapes.small)
//                .clickable {
//                    selected = !selected
//                }
                .fillMaxWidth()
//                .fillMaxHeight(if (selected) 0.6f else 0.2f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(),
                contentAlignment = Alignment.TopCenter
            ){
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(text = "스터디 분야")
                    SetRequireDeveloperLayout(
                        developerEnumList = developerEnumList,
                        viewModel = viewModel,
                        onDevSelected = onDevSelected,
                    )
                }
            }
        }
    }
}






//필요로하는 개발자 역할
@Composable
fun SetRequireDeveloperLayout(
    developerEnumList: List<DeveloperEnum>,
    viewModel: CreateStudyViewModel,
    onDevSelected: (selected: Boolean, answer: DeveloperEnum) -> Unit,
) {
    val sublist = developerEnumList
    val selectedList = remember{ mutableStateOf(viewModel.developerEnumList) }

    LazyColumn {
        items(sublist.chunked(3)) { rowItems ->
            LazyRow {
                items(rowItems) { item ->
                    val selected = remember{ mutableStateOf( selectedList.value.contains(item)) }

                    DeveloperListItemCard(
                        item,
                        selected = selected,
                        viewModel = viewModel,
                        onDevSelected = onDevSelected
                    )
                }
            }
        }
    }
}

@Composable
fun DeveloperListItemCard(
    item: DeveloperEnum,
    selected: MutableState<Boolean>,
    viewModel: CreateStudyViewModel,
    onDevSelected: (selected: Boolean, answer: DeveloperEnum) -> Unit
) {
    val context = LocalContext.current
    // 각각의 아이템을 표시할 컴포저블을 만들어줍니다.
    // 예를 들어, 아래에서는 간단한 텍스트를 표시합니다.
    val buttonColor = if (selected.value) {
        Color(47,147,143)
    } else {
        Color(107,197,193)
    }
    Button(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(0.3f),
        onClick = {
            if(viewModel.developerEnumList.size == 3 && !selected.value){
                Toast.makeText(context, "최대 3개 선택할 수 있어요!", Toast.LENGTH_SHORT).show()
            }else{
                selected.value = !selected.value
                onDevSelected(selected.value, item)
            }
        },

        colors = ButtonColors(
            containerColor = if (selected.value) {
                Color(47,147,143)
            } else {
                Color(107,197,193)
            },
            contentColor= Color.White,
            disabledContainerColor = MaterialTheme.colorScheme.outline,
            disabledContentColor = Color.White
        )
    )
    {
        Text(
            text = item.info,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}