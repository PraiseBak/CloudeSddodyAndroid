package com.study.sddodyandroid.component.main.createstudy

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.study.sddodyandroid.common.CreateStudyViewModel
import com.study.sddodyandroid.ui.component.main.create.QuestionWrapper
import kotlin.math.roundToInt

@Composable
fun SetSliderMaxMember(
    @StringRes titleResourceId: Int,
    viewModel: CreateStudyViewModel,
    value: MutableState<Int>,
    onValueChange: (Int) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0.2f..20f,
    steps: Int = 200,
    @StringRes startTextResource: Int,
    @StringRes endTextResource: Int,
    modifier: Modifier = Modifier,
) {
    var sliderPosition by remember {
        mutableStateOf(0.2f)
    }
    QuestionWrapper(
        titleResourceId = titleResourceId,
        modifier = modifier,
    ) {

        Row {
            Slider(
                value = sliderPosition,
                onValueChange = {
                    sliderPosition = it
                    onValueChange((it*10).roundToInt())
                    Log.d("slider","${viewModel.maxMember.value}")
                },
                valueRange = valueRange,
                steps = steps,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            )
        }
        Row {
            Text(
                text = stringResource(id = startTextResource),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.8f)
            )
            Text(
                text = "모집 인원수: ${(sliderPosition * 10).roundToInt()}",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.8f)
            )
            Text(
                text = stringResource(id = endTextResource),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.8f)
            )
        }
    }
}
