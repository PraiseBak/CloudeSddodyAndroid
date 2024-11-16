package com.study.sddodyandroid.helper.study

/**
 * PREPARATION은 유저 추가가 가능
 * STUDYING은 더이상 유저 추가를 받지 않음
 * END는 스터디 종료
 */
enum class StudyStateEnum (val text: String){
    PREPARATION("참여가능"),
    STUDYING("진행중"),
    END("종료")
}
