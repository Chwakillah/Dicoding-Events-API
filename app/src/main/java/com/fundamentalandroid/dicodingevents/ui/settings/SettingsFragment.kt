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
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkInfo
import com.fundamentalandroid.dicodingevents.data.preferences.SettingPreferences
import com.fundamentalandroid.dicodingevents.data.workers.NotificationWorker
import com.fundamentalandroid.dicodingevents.helper.ViewModelFactory
import com.fundamentalandroid.dicodingevents.ui.main.MainViewModel
import com.fundamentalandroid.dicodingevents.databinding.FragmentSettingsBinding // Import the generated binding class
import java.util.concurrent.TimeUnit

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("SettingsFragment", "onCreateView called")

        _binding = FragmentSettingsBinding.inflate(inflater, container, false) // Inflate using binding
        val view = binding.root // Get the root view from binding

        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(MainViewModel::class.java)

        setupThemeSwitch()
        setupReminderSwitch()

        return view
    }

    private fun setupThemeSwitch() {
        mainViewModel.getThemeSettings().observe(viewLifecycleOwner) { isNightMode ->
            binding.switchTheme.isChecked = isNightMode // Use binding to access the switch
            AppCompatDelegate.setDefaultNightMode(
                if (isNightMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            Log.d("SettingsFragment", "Theme changed to: ${if (isNightMode) "Dark" else "Light"}")
        }

        binding.switchTheme.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            mainViewModel.saveThemeSetting(isChecked)

            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            Log.d("SettingsFragment", "Switch changed to: $isChecked")
        }
    }

    private fun setupReminderSwitch() {
        // Observe reminder setting from ViewModel
        mainViewModel.getReminderSettings().observe(viewLifecycleOwner) { isReminderEnabled ->
            binding.switchNotification.isChecked = isReminderEnabled // Use binding
            setupDailyReminder(isReminderEnabled)
        }

        binding.switchNotification.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            mainViewModel.saveReminderSetting(isChecked)
            setupDailyReminder(isChecked)
            Log.d("SettingsFragment", "Reminder switch changed to: $isChecked")
        }
    }

    private fun setupDailyReminder(isEnabled: Boolean) {
        val workManager = WorkManager.getInstance(requireContext())

        if (isEnabled) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val dailyWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            workManager.enqueueUniquePeriodicWork(
                "eventReminder",
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyWorkRequest
            )
            Log.d("SettingsFragment", "Daily reminder enabled")
        } else {
            workManager.cancelUniqueWork("eventReminder")
            Log.d("SettingsFragment", "Daily reminder disabled")
        }
    }

    private fun observeWorkStatus() {
        WorkManager.getInstance(requireContext())
            .getWorkInfosForUniqueWorkLiveData("eventReminder")
            .observe(viewLifecycleOwner) { workInfoList ->
                workInfoList?.forEach { workInfo ->
                    when (workInfo.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            Log.d("SettingsFragment", "Worker completed successfully")
                        }
                        WorkInfo.State.FAILED -> {
                            Log.e("SettingsFragment", "Worker failed")
                        }
                        WorkInfo.State.RUNNING -> {
                            Log.d("SettingsFragment", "Worker is running")
                        }
                        else -> {
                            Log.d("SettingsFragment", "Worker state: ${workInfo.state}")
                        }
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference
    }
}
