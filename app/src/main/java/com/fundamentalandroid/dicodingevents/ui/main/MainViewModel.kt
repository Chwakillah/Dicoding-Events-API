package com.fundamentalandroid.dicodingevents.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fundamentalandroid.dicodingevents.data.preferences.SettingPreferences
import kotlinx.coroutines.launch

class MainViewModel(private val pref: SettingPreferences) : ViewModel() {

    fun getThemeSettings() = pref.getThemeSetting().asLiveData()

    fun saveThemeSetting(isDarkMode: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkMode)
        }
    }
}