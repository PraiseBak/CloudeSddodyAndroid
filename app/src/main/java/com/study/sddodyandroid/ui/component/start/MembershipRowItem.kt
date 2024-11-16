package com.example.test.ui.component

//import androidx.compose.foundation.layout.FlowRowScopeInstance.weight
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.study.sddodyandroid.common.CheckedEnumViewModel
import com.study.sddodyandroid.helper.CheckedEnum
import com.study.sddodyandroid.ui.component.start.FlowRow
import com.study.sddodyandroid.ui.component.start.MembershipRowItemButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembershipRowItem(
    @StringRes titleResourceId: Int,
    infoEnum: List<CheckedEnum>,
    selectedList: MutableList<CheckedEnum>,
    onSelected: (CheckedEnum) -> Unit,
    onDeselected: (CheckedEnum) -> Unit,
    viewModel: CheckedEnumViewModel,
    clickCount: MutableState<Int>,
    isButtonDisabled: Boolean
) {

    FlowRow(
        verticalGap = 8.dp,
        horizontalGap = 8.dp,
        alignment = Alignment.Start,
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
            infoEnum.forEach { select ->

                selectedList.forEach {
                    if(it.infoEnum.info.equals(select.infoEnum.info)) {
                        select.isChecked = true
                    }
                }

                MembershipRowItemButton(
                    text = select.infoEnum.info,
                    isSelected = select.isChecked,
                    onSelected = {
                        onSelected(select)
                    },
                    onDeselected = {
                        onDeselected(select)
                    },
                    CheckedEnum = select,
                    clickCount,
                    isButtonDisabled,
//
                    modifier = Modifier
                        .padding(start = 5.dp,end = 5.dp)
                    )
            }
        }

    }

