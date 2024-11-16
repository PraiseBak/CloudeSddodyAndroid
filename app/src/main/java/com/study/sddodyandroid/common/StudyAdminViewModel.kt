package com.study.sddodyandroid.common

import androidx.lifecycle.ViewModel
import com.study.sddodyandroid.dto.StudyResponseDto


class StudyAdminViewModel : ViewModel() {
    private var  studyResponseDto : StudyResponseDto? = null

    fun updateStudyDto(studyResponseDto: StudyResponseDto){
        this.studyResponseDto = studyResponseDto
    }
    fun getStudyDto() : StudyResponseDto?{
        return studyResponseDto
    }

}
