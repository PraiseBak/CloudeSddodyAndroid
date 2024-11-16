package com.study.sddodyandroid.dto

import androidx.compose.runtime.MutableState
import com.google.android.gms.maps.model.LatLng
import com.study.sddodyandroid.helper.member.DevLevelEnum
import com.study.sddodyandroid.helper.DeveloperEnum
import com.study.sddodyandroid.helper.FrameworkEnum
import com.study.sddodyandroid.helper.LanguageEnum
import com.study.sddodyandroid.helper.member.DevYearEnum

data class MemberDto(
        var kakaoId: Long = 0,
        var _nickname: MutableState<String>,
        var _selfIntroduce : MutableState<String>,
        var nickname : String,
        var selfIntroduce : String,
        var memberLocation: LatLng,
        var interestField: List<DeveloperEnum>,
        var haveExperienceTechStack: List<FrameworkEnum>,
        var devLanguageField: List<LanguageEnum>,
        var profileImg: String,
        //개발 수준
        var devLevel : DevLevelEnum,
        //개발 몇년정도했는지
        var devYear : DevYearEnum
)