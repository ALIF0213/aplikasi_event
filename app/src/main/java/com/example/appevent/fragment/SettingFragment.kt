package com.example.appevent.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.appevent.NotificationWorker
import com.example.appevent.PreferencesHelper
import com.example.appevent.R
import com.example.appevent.dataStore
import com.example.appevent.ThemePreference
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SettingsFragment : Fragment() {

    private lateinit var darkModeSwitch: SwitchCompat
    private lateinit var themePreference: ThemePreference
    private lateinit var notificationSwitch: SwitchCompat
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        darkModeSwitch = view.findViewById(R.id.switch_dark_mode)
        preferencesHelper = PreferencesHelper(requireContext())
        notificationSwitch = view.findViewById(R.id.switch_notification)
        themePreference = ThemePreference.getInstance(requireContext().dataStore)

        val isNotificationEnabled = preferencesHelper.getNotificationSetting()
        notificationSwitch.isChecked = isNotificationEnabled

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            preferencesHelper.saveNotificationSetting(isChecked)
            if (isChecked) {
                startDailyNotification()
            } else {
                stopDailyNotification()
            }
        }

        lifecycleScope.launch {
            themePreference.getThemeSetting().collect { isDarkModeActive ->
                darkModeSwitch.isChecked = isDarkModeActive
                setThemeMode(isDarkModeActive)
            }
        }

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                themePreference.saveThemeSetting(isChecked)
                setThemeMode(isChecked)
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferencesHelper = PreferencesHelper(requireContext())

        notificationSwitch = view.findViewById(R.id.switch_notification)

        val isNotificationEnabled = preferencesHelper.getNotificationSetting()
        notificationSwitch.isChecked = isNotificationEnabled

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            preferencesHelper.saveNotificationSetting(isChecked)

            if (isChecked) {
                startDailyNotification()
            } else {
                stopDailyNotification()
            }
        }
    }

    private fun startDailyNotification() {
        val dailyWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(requireContext()).enqueue(dailyWorkRequest)
    }

    private fun stopDailyNotification() {
        WorkManager.getInstance(requireContext()).cancelAllWorkByTag("NotificationWorker")
    }

    private fun setThemeMode(isDarkModeActive: Boolean) {
        if (isDarkModeActive) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
