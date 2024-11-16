package com.study.sddodyandroid.component.main.createstudy

import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.study.sddodyandroid.common.CreateStudyViewModel
import com.study.sddodyandroid.helper.FrameworkEnum
import com.study.sddodyandroid.helper.member.DevLevelEnum
import com.study.sddodyandroid.helper.member.DevYearEnum
import com.study.sddodyandroid.ui.component.main.create.QuestionWrapper

@Composable
fun SetStudyDevFramework(
    @StringRes titleResourceId: Int,
    frameworkEnumList: List<FrameworkEnum>,
    viewModel: CreateStudyViewModel,
    onFrameworkSelected: (selected: Boolean, answer: FrameworkEnum) -> Unit,
    modifier: Modifier = Modifier,
){
    QuestionWrapper(
        titleResourceId = titleResourceId,
        modifier = modifier,
    ){
//        var selected by remember { mutableStateOf(false) }

        LazyColumn{

            item{
                Text(text = "필요로하는 개발자에 대해 알려주세요!")
                HorizontalDivider()

                SetRequireDeveloperInfo(
                    viewModel = viewModel
                )

                Text(text = "필요로하는 프레임워크를 알려주세요!")
                HorizontalDivider()

                SetRequireFrameworkLayout(
                    frameworkEnumList = frameworkEnumList,
                    viewModel = viewModel,
                    onDevSelected = onFrameworkSelected,
                )
            }


        }


    }
}

//필요로하는 개발자에 대한 정보 입력
@Composable
fun SetRequireDeveloperInfo(viewModel: CreateStudyViewModel) {
    var devLevelSelectIdx by remember { mutableIntStateOf(0) }
    var devYearSelectIdx by remember { mutableIntStateOf(0) }

    DevLevelEnum.entries.forEachIndexed { index, it ->
        if(it.text == viewModel.devLevel.text){
            devLevelSelectIdx = index
        }
    }

    DevYearEnum.entries.forEachIndexed { index, it ->
        if(it.text == viewModel.devYear.text){
            devYearSelectIdx = index
        }
    }

    DevYearEnum.entries.forEachIndexed { index, it ->
        Button(
            onClick = {
                viewModel.devYear = it
                devYearSelectIdx = index
            },

            colors =ButtonColors(
                containerColor = if (index == devYearSelectIdx) {
                    Color(47,147,143)
                } else {
                    Color(107,197,193)
                },
                contentColor= Color.White,
                disabledContainerColor = MaterialTheme.colorScheme.outline,
                disabledContentColor = Color.White
            )) {
            Text(it.text)
        }
    }

    //개발 수준
    Text(text = "어떤 직종이 적합할까요?")
    HorizontalDivider()

    Column{
        DevLevelEnum.entries.forEachIndexed { index, it ->
            Button(
                onClick = {
                    viewModel.devLevel = it
                    devLevelSelectIdx = index
                },
                colors = ButtonColors(
                    containerColor = if (index == devLevelSelectIdx) {
                        Color(47,147,143)
                    } else {
                        Color(107,197,193)
                    },
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.outline,
                    disabledContentColor = Color.White
                )
            ) {
                Text(it.text)
            }
        }

    }


}

//할줄알아야하는 프레임워크
@Composable
fun SetRequireFrameworkLayout(
    frameworkEnumList: List<FrameworkEnum>,
    viewModel: CreateStudyViewModel,
    onDevSelected: (selected: Boolean, answer: FrameworkEnum) -> Unit,
) {
    val selectedList = remember{ mutableStateOf(viewModel.frameworkEnumList) }
    val sublist = frameworkEnumList
    val chunkedList = sublist.chunked(3)

    chunkedList.forEachIndexed{index,it ->
        Column {
            LazyRow {
                items(chunkedList[index]) { item ->
                    val selected = remember {mutableStateOf( selectedList.value.contains(item))}
                    FrameworkListItemCard(
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
fun FrameworkListItemCard(
    item: FrameworkEnum,
    selected: MutableState<Boolean>,
    viewModel: CreateStudyViewModel,
    onDevSelected: (selected: Boolean, answer: FrameworkEnum) -> Unit
) {
    val context = LocalContext.current
    Button(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(0.3f),
        onClick = {
            if(viewModel.frameworkEnumList.size == 3 && !selected.value){
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


