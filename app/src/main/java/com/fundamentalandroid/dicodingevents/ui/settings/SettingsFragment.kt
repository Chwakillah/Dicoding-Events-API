package com.fundamentalandroid.dicodingevents.ui.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.fundamentalandroid.dicodingevents.R
import com.fundamentalandroid.dicodingevents.data.preferences.SettingPreferences
import com.fundamentalandroid.dicodingevents.helper.ViewModelFactory
import com.fundamentalandroid.dicodingevents.ui.main.MainViewModel
import com.google.android.material.switchmaterial.SwitchMaterial

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsFragment : Fragment() {

    private lateinit var switchTheme: SwitchMaterial
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("SettingsFragment", "onCreateView called")

        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        switchTheme = view.findViewById(R.id.switch_theme)

        Log.d("SettingsFragment", "switchTheme initialized: $switchTheme")

        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(MainViewModel::class.java)

        mainViewModel.getThemeSettings().observe(viewLifecycleOwner) { isNightMode ->
            switchTheme.isChecked = isNightMode
            AppCompatDelegate.setDefaultNightMode(
                if (isNightMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            Log.d("SettingsFragment", "Theme changed to: ${if (isNightMode) "Dark" else "Light"}")
        }

        switchTheme.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            mainViewModel.saveThemeSetting(isChecked)

            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            Log.d("SettingsFragment", "Switch changed to: $isChecked")
        }

        return view
    }

}
