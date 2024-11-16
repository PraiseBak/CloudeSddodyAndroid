package com.study.sddodyandroid.component.main.createstudy

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.study.sddodyandroid.R
import com.study.sddodyandroid.common.CreateStudyViewModel
import com.study.sddodyandroid.helper.DeveloperEnum
import com.study.sddodyandroid.helper.FrameworkEnum
import com.study.sddodyandroid.ui.component.main.create.SetStudyTitleAndContent
import com.study.sddodyandroid.ui.component.main.create.SetStudyImage

@Composable
fun SetStudyTitleAndContentView(
    onOptionSelected: (answer: String) -> Unit,
    viewModel: CreateStudyViewModel,
    modifier: Modifier,
){
    SetStudyTitleAndContent(
        titleResourceId = R.string.in_my_free_time,
        onOptionSelected = onOptionSelected,
        viewModel = viewModel,
        modifier = modifier,
    )
}


@Composable
fun SetStudyImageView(
    imageUri: MutableList<Uri>,
    viewModel: CreateStudyViewModel,
    getNewImageUri: () -> Uri,
    onPhotoTaken: (MutableList<Uri>) -> Unit,
    modifier: Modifier = Modifier,
){
    SetStudyImage(
        titleResourceId = R.string.selfie_skills,
        imageUri = imageUri,
        viewModel = viewModel,
        getNewImageUri = getNewImageUri,
        onPhotoTaken = onPhotoTaken,
        modifier = modifier,
    )
}

@Composable
fun SetActiveLocation(
    location: MutableState<LatLng>,
    viewModel: CreateStudyViewModel,
    fusedLocationClient: FusedLocationProviderClient,
    modifier: Modifier
){
    SetLocation(
        titleResourceId = R.string.active_location,
        location = location,
        viewModel = viewModel,
        fusedLocationProviderClient = fusedLocationClient,
        modifier = modifier
    )
}

@Composable
fun SetAllowStudyRoom(
    allowStudy: List<Int>,
    viewModel: CreateStudyViewModel,
    onOptionSelected: (selected: Boolean, answer: Int) -> Unit,
    modifier: Modifier
){
    AllowStudyRoom(
        titleResourceId = R.string.allow_study,
        viewModel = viewModel,
        possibleAnswers = listOf(
            R.string.not_allow,
            R.string.allow,
        ),
        onOptionSelected = onOptionSelected,
        allowStudy = allowStudy,
        modifier = modifier
    )
}

@Composable
fun SetRequireDeveloper(
    developerEnumList: List<DeveloperEnum>,
    viewModel: CreateStudyViewModel,
    onDevSelected: (selected: Boolean, answer: DeveloperEnum) -> Unit,
    modifier: Modifier
){
    SetStudyDeveloper(
        titleResourceId = R.string.require_dev,
        developerEnumList = developerEnumList,
        viewModel = viewModel,
        onDevSelected = onDevSelected,
        modifier = modifier
    )
}
@Composable
fun SetRequireDevFramework(
    frameworkEnumList: List<FrameworkEnum>,
    viewModel: CreateStudyViewModel,
    onFrameworkSelected: (selected: Boolean, answer: FrameworkEnum) -> Unit,
    modifier: Modifier
){
    SetStudyDevFramework(
        titleResourceId = R.string.require_frame,
        frameworkEnumList = frameworkEnumList,
        viewModel = viewModel,
        onFrameworkSelected = onFrameworkSelected,
        modifier = modifier
    )
}

@Composable
fun SetMaxMember(
    value: MutableState<Int>,
    onValueChange: (Int) -> Unit,
    viewModel: CreateStudyViewModel,
    modifier: Modifier = Modifier
){
    SetSliderMaxMember(
        titleResourceId = R.string.max_member,
        viewModel = viewModel,
        value = value,
        onValueChange = onValueChange,
        startTextResource = R.string.member_start,
        endTextResource = R.string.member_end,
        modifier = modifier
    )
}