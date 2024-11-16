package com.study.sddodyandroid.dto

import com.study.sddodyandroid.helper.AlarmCategory

data class AlarmDto(
    //해당 알람의 정보
    val info : String,
    //이동할 uri의 id부분
    val moveUriId : Long = 0,
    //읽었는지 여부
    val isRead : Boolean = false,
    //알람의 유형
    val alarmCategory: AlarmCategory
) {


}
