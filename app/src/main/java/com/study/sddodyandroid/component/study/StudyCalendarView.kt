package com.study.sddodyandroid.component.study

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kakao.sdk.common.KakaoSdk.init
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StudyCalendarView() {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val currentMonth = YearMonth.now()
    val daysInMonth = currentMonth.lengthOfMonth()
    var showDialog by remember { mutableStateOf(false) }
    val schedules = remember {
        mutableStateOf(
            mutableMapOf<LocalDate, MutableList<String>>().apply {
                val now = LocalDate.now()
                val after = LocalDate.now().plusDays(3)
                getOrPut(now) { mutableListOf() }.apply {
                    add("디자인 완성")
                    add("기능 명세서 작성")
                }
                getOrPut(after) { mutableListOf() }.add("기능 명세서 작성")
            }
        )
    }
    Column(modifier = Modifier.fillMaxSize().padding(0.dp)) {
        Text(
            text = "${currentMonth.month} ${currentMonth.year}",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.align(Alignment.CenterHorizontally)

        )

        Column {
            val weeks = (1..daysInMonth).chunked(7)
            for (week in weeks) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (day in week) {
                        val date = LocalDate.of(currentMonth.year, currentMonth.month, day)
                        DayView(
                            day = day,
                            selectedDate = selectedDate,
                            schedules = schedules.value,
                            onDateSelected = {
                                showDialog = true
                                selectedDate = date
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (showDialog) {
            ScheduleInputDialog(
                selectedDate = selectedDate,
                schedules = schedules,
                onDismissRequest = { showDialog = false }
            )
        }
    }
}

@Composable
fun ScheduleInputDialog(
    selectedDate: LocalDate,
    schedules: MutableState<MutableMap<LocalDate, MutableList<String>>>,
    onDismissRequest: () -> Unit
) {
    var scheduleText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("Add Schedule for ${selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = scheduleText,
                    onValueChange = { scheduleText = it },
                    label = { Text("Schedule") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    schedules.value.getOrPut(selectedDate) { mutableListOf() }.add(scheduleText)
                    scheduleText = ""
                    onDismissRequest()
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun DayView(day: Int, selectedDate: LocalDate, schedules: Map<LocalDate, List<String>>, onDateSelected: (Int) -> Unit) {
    val isSelected = selectedDate.dayOfMonth == day
    val daySchedules = schedules[LocalDate.of(selectedDate.year, selectedDate.month, day)] ?: emptyList()

    // 동적으로 셀 높이를 조절
    val cellHeight = if (daySchedules.isNotEmpty()) 80.dp else 60.dp // 일정이 있으면 높이 늘림

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .heightIn(min = 60.dp, max = 120.dp) // 최소 높이와 최대 높이를 설정하여 일정에 따라 크기를 조절
            .width(60.dp)
            .background(
                if (isSelected) Color.LightGray else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { onDateSelected(day) }
            .padding(4.dp)
    ) {
        Text(
            text = day.toString(),
            textAlign = TextAlign.Center
        )

        // 일정을 스크롤 가능하게 표시
        if (daySchedules.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxHeight()) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()) // 일정이 많으면 스크롤 가능하도록 설정
                ) {
                    // 일정들을 중첩해서 표시
                    daySchedules.take(5).forEach { schedule -> // 최대 5개 일정 표시
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = schedule,
                            style = MaterialTheme.typography.caption,
                            maxLines = 1,
                            color = Color.White,
                            modifier = Modifier
                                .background(Color.Blue, shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    // 일정이 더 있으면 표시
                    if (daySchedules.size > 5) {
                        Text(
                            text = "+${daySchedules.size - 5}",
                            style = MaterialTheme.typography.caption,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
