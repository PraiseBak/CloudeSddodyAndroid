package com.study.sddodyandroid.component.main.createstudy

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Bottom
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Horizontal
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.study.sddodyandroid.R
import com.study.sddodyandroid.common.CreateStudyScreenData
import com.study.sddodyandroid.ui.theme.stronglyDeemphasizedAlpha
import com.study.sddodyandroid.ui.utils.supportWideScreen

@Composable
fun CreateStudyScaffold(
    createStudyScreenData: CreateStudyScreenData,
    isNextEnabled: Boolean,
    onClosePressed: () -> Unit,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
    onDonePressed: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
){
    Surface(modifier = Modifier.supportWideScreen()) {
        Scaffold(
            topBar = {
                SurveyTopAppBar(
                    questionIndex = createStudyScreenData.questionIndex,
                    totalQuestionsCount = createStudyScreenData.questionCount,
                    onClosePressed = onClosePressed,
                )
            },
            content = content,
            bottomBar = {
                SurveyBottomBar(
                    shouldShowPreviousButton = createStudyScreenData.shouldShowPreviousButton,
                    shouldShowDoneButton = createStudyScreenData.shouldShowDoneButton,
                    isNextButtonEnabled = isNextEnabled,
                    onPreviousPressed = onPreviousPressed,
                    onNextPressed = onNextPressed,
                    onDonePressed = onDonePressed
                )
            }
        )
    }
}

@Composable
private fun TopAppBarTitle(
    questionIndex: Int,
    totalQuestionsCount: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = (questionIndex + 1).toString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = stronglyDeemphasizedAlpha)
        )
        Text(
            text = stringResource(R.string.question_count, totalQuestionsCount),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class) // CenterAlignedTopAppBar is experimental in m3
@Composable
fun SurveyTopAppBar(
    questionIndex: Int,
    totalQuestionsCount: Int,
    onClosePressed: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        CenterAlignedTopAppBar(
            title = {
                TopAppBarTitle(
                    questionIndex = questionIndex,
                    totalQuestionsCount = totalQuestionsCount,
                )
            },
            actions = {
                IconButton(
                    onClick = onClosePressed,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = stringResource(id = R.string.close),
                        tint = MaterialTheme.colorScheme.onSurface.copy(stronglyDeemphasizedAlpha)
                    )
                }
            }
        )

        val animatedProgress by animateFloatAsState(
            targetValue = (questionIndex + 1) / totalQuestionsCount.toFloat(),
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
        )
        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        )
    }
}

@Composable
fun SurveyBottomBar(
    shouldShowPreviousButton: Boolean,
    shouldShowDoneButton: Boolean,
    isNextButtonEnabled: Boolean,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
    onDonePressed: () -> Unit
) {
    Surface(shadowElevation = 7.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Since we're not using a Material component but we implement our own bottom bar,
                // we will also need to implement our own edge-to-edge support. Similar to the
                // NavigationBar, we add the horizontal and bottom padding if it hasn't been consumed yet.
                .windowInsetsPadding(WindowInsets.systemBars.only(Horizontal + Bottom))
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            if (shouldShowPreviousButton) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = onPreviousPressed
                ) {
                    Text(text = stringResource(id = R.string.previous))
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            if (shouldShowDoneButton) {
                val onceClicked = remember {mutableStateOf(false)}
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = {
                        onDonePressed()
                        onceClicked.value = true
                              },
                    enabled = !onceClicked.value,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(47,147,143)

                    )
                ) {

                    Text(text = stringResource(id = R.string.done))
                }
            } else {
                Button(
                    colors =  ButtonDefaults.buttonColors(
                        containerColor =
                            Color(47,147,143)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = onNextPressed,
                    enabled = isNextButtonEnabled,
                ) {
                    Text(text = stringResource(id = R.string.next))
                }
            }
        }
    }
}
