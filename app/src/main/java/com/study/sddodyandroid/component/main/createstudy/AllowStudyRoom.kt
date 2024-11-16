package com.study.sddodyandroid.component.main.createstudy

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.study.sddodyandroid.common.CreateStudyViewModel
import com.study.sddodyandroid.ui.component.main.create.QuestionWrapper

@Composable
fun AllowStudyRoom(
    @StringRes titleResourceId: Int,
    viewModel: CreateStudyViewModel,
    possibleAnswers: List<Int>,
    onOptionSelected: (selected: Boolean, answer: Int) -> Unit,
    allowStudy: List<Int>,
    modifier: Modifier = Modifier,
){
    val context = LocalContext.current
    QuestionWrapper(
        titleResourceId = titleResourceId,
        modifier = modifier,
    ){
        onOptionSelected(true,0)
        var selectedOption: Int by remember { mutableIntStateOf(0) }
        possibleAnswers.forEachIndexed{ index,it ->
            var selected = selectedOption == index
            CheckboxRow(
                modifier = Modifier.padding(vertical = 8.dp),
                text = stringResource(id = it),
                selected = selected,
                onOptionSelected = {
                    selectedOption = index
                    if(selectedOption == 0){
                        viewModel.isOpenToEveryone.value = true
                    }else{
                        viewModel.isOpenToEveryone.value = false
                    }
                    onOptionSelected(selected, it)
                    Log.d("checkboxrow", "${viewModel.isOpenToEveryone.value}")
                }
            )
        }
    }
}

@Composable
fun CheckboxRow(
    text: String,
    selected: Boolean,
    onOptionSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onOptionSelected)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Box(Modifier.padding(8.dp)) {
                Checkbox(selected, onCheckedChange = null)
            }
        }
    }
}