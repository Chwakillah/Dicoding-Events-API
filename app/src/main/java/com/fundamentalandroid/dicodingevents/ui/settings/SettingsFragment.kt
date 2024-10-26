package com.fundamentalandroid.dicodingevents.ui.settings

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fundamentalandroid.dicodingevents.data.preferences.SettingPreferences
import com.fundamentalandroid.dicodingevents.databinding.FragmentSettingsBinding
import com.fundamentalandroid.dicodingevents.helper.ViewModelFactory
import com.fundamentalandroid.dicodingevents.ui.main.MainViewModel
import com.fundamentalandroid.dicodingevents.data.workers.NotificationWorker
import java.util.concurrent.TimeUnit
import android.content.res.Configuration

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private var isInitialSetup = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]

        binding.switchTheme.isEnabled = false

        setupThemeSwitch()
        setupReminderSwitch()

        return view
    }

    private fun setupThemeSwitch() {
        binding.switchTheme.setOnCheckedChangeListener(null)

        mainViewModel.getThemeSettings().observe(viewLifecycleOwner) { isNightMode ->
            try {
                val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                val systemIsNightMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES

                if (isInitialSetup) {
                    binding.switchTheme.isChecked = isNightMode
                    isInitialSetup = false
                }

                if (isNightMode != systemIsNightMode) {
                    AppCompatDelegate.setDefaultNightMode(
                        if (isNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                    )
                }

                binding.switchTheme.isEnabled = true

                binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
                    if (mainViewModel.getThemeSettings().value != isChecked) {
                        mainViewModel.saveThemeSetting(isChecked)
                    }
                }
            } catch (e: Exception) {
                binding.switchTheme.isEnabled = true
                e.printStackTrace()
            }
        }
    }

    private fun setupReminderSwitch() {
        mainViewModel.getReminderSettings().observe(viewLifecycleOwner) { isReminderEnabled ->
            binding.switchNotification.isChecked = isReminderEnabled
            setupDailyReminder(isReminderEnabled)
        }

        binding.switchNotification.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestNotificationPermission()
                    binding.switchNotification.isChecked = false
                } else {
                    mainViewModel.saveReminderSetting(isChecked)
                    setupDailyReminder(isChecked)
                }
            } else {
                mainViewModel.saveReminderSetting(isChecked)
                setupDailyReminder(isChecked)
            }
        }
    }

    private fun requestNotificationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.POST_NOTIFICATIONS)) {
            AlertDialog.Builder(requireContext())
                .setTitle("Permission Needed")
                .setMessage("This permission is needed to show notifications for event reminders.")
                .setPositiveButton("OK") { _, _ ->
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        0
                    )
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }

    private fun setupDailyReminder(isEnabled: Boolean) {
        val workManager = WorkManager.getInstance(requireContext())

        if (isEnabled) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val dailyWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                1, TimeUnit.DAYS
            )
                .setConstraints(constraints)
                .build()

            workManager.enqueueUniquePeriodicWork(
                "eventReminder",
                ExistingPeriodicWorkPolicy.UPDATE,
                dailyWorkRequest
            )
        } else {
            workManager.cancelUniqueWork("eventReminder")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
