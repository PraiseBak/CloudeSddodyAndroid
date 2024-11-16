package com.study.sddodyandroid.ui.component.main.create

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.study.sddodyandroid.common.CreateStudyViewModel

@Composable
fun SetStudyTitleAndContent(
    @StringRes titleResourceId: Int,
    viewModel: CreateStudyViewModel,
    onOptionSelected: (answer: String) -> Unit,
    modifier: Modifier,
){
    //글자수에따라 해야하는데
    QuestionWrapper(
        modifier = modifier.verticalScroll(rememberScrollState()),
        titleResourceId = titleResourceId,
    ){
        val MAX_TITLE_LENGTH = 50
        val MAX_BODY_LENGTH = 4000
        OutlinedTextField(
            value = viewModel.title.value,
            onValueChange = {
                if (it.length <= MAX_TITLE_LENGTH) {
                    viewModel.onTitleResponse(it)
                }
            },

            label = { Text(text = "제목/스터디명을 적어주세요") },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                Text(
                    text = "${viewModel.title.value.length} / $MAX_TITLE_LENGTH",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            }
        )

        OutlinedTextField(
            value = viewModel.content.value,
            onValueChange = {
                if (it.length <= MAX_BODY_LENGTH) {
                    viewModel.onContentResponse(it)
                }

            },
            label = { Text(text = "내용을 적어주세요") },
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth(),
            supportingText = {
                Text(
                    text = "${viewModel.content.value.length} / $MAX_BODY_LENGTH",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            }
        )

    }

}