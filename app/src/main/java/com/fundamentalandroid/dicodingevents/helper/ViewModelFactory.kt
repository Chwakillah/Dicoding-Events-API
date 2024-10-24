package com.fundamentalandroid.dicodingevents.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fundamentalandroid.dicodingevents.data.preferences.SettingPreferences
import com.fundamentalandroid.dicodingevents.ui.main.MainViewModel

class ViewModelFactory(private val pref: SettingPreferences) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}