package com.study.sddodyandroid.common

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

// ViewModel
class SearchViewModel : ViewModel() {
    private val _currentScreen = mutableStateOf<SearchScreen>(SearchScreen.Search)
    val currentScreen: State<SearchScreen> = _currentScreen

    fun setSearchScreen(screen: SearchScreen) {
        _currentScreen.value = screen
    }
}

sealed class SearchScreen {
    object Search : SearchScreen()
    object Results : SearchScreen()
}