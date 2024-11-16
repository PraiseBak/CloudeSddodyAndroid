package com.study.sddodyandroid.ui.component.start

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.example.test.ui.component.MembershipRowItem
import com.study.sddodyandroid.common.CheckedEnumViewModel
import com.study.sddodyandroid.helper.CheckedEnum
import com.study.sddodyandroid.helper.DeveloperEnum
import com.study.sddodyandroid.helper.FrameworkEnum
import com.study.sddodyandroid.helper.InfoEnum
import com.study.sddodyandroid.helper.LanguageEnum

@SuppressLint("SuspiciousIndentation")
@Composable
fun SetEnumRowItem(
    @StringRes titleResourceId: Int,
    type: String,
    viewModel: CheckedEnumViewModel,
){
    var value = getValue(type,viewModel)
    val checkedEnumList = CheckedEnum.convertCheckedEnum(value as Array<out InfoEnum>)
    // 각 버튼의 카운터
    var clickCount = remember { mutableStateOf(0) }

    // 세 번 클릭 후 버튼 비활성화
    val isButtonDisabled = clickCount.value >= 3
    //리스트 분류
    var selectedList = viewModel.setTypeToList(type)
    var currentRowWidth = 0.dp


    clickCount.value = selectedList.size

    val sublist : List<CheckedEnum> = checkedEnumList.toList()
        MembershipRowItem(
            titleResourceId = titleResourceId,
            infoEnum = sublist,
            selectedList = selectedList,
            onSelected = { selected ->


                viewModel.addItemToSelectedList(type,selected)
                clickCount.value += 1
            },
            onDeselected = { deselected ->
                viewModel.removeItemFromList(type,deselected)
                clickCount.value -= 1
            },
            viewModel,
            clickCount,
            isButtonDisabled
        )
}

fun getValue(type: String, viewModel: CheckedEnumViewModel): Array<out InfoEnum>?{
    var value : Array<out InfoEnum>? = null
    if(type.equals("language")){
        value = LanguageEnum.values()
    }
    else if(type.equals("developer")){
        value = DeveloperEnum.values()
    }
    else if(type.equals("framework")){
        value = FrameworkEnum.values()
    }

    return value
}


