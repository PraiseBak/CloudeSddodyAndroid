package com.study.sddodyandroid.ui.component.main.study

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.study.sddodyandroid.dto.StudyRequestDto

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarDateTimeAndParticipantIcon(studyRequestDto: StudyRequestDto){
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(top = 5.dp),
    ) {

        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = "Calendar Icon",
            tint = Color.Gray
        )
//        Text(
//            text = formatDateTime(LocalDateTime.now()),
//            style = MaterialTheme.typography.bodyMedium,
//            textAlign = TextAlign.Center
//        )
        Icon(
            imageVector = Icons.Default.Group,
            contentDescription = "Participant Icon",
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "1 / ${studyRequestDto.maxStudyMemberNum}",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}
