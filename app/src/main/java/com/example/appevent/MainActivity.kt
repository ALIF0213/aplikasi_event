package com.example.appevent

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.appevent.fragment.FavoriteFragment
import com.example.appevent.fragment.PastFragment
import com.example.appevent.fragment.UpcomingFragment
import com.example.appevent.fragment.HomeFragment
import com.example.appevent.fragment.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val eventName = intent.getStringExtra("EVENT_NAME")
        val eventTime = intent.getStringExtra("EVENT_TIME")

        if (!eventName.isNullOrEmpty() && !eventTime.isNullOrEmpty()) {
            Toast.makeText(this, "Event: $eventName starts at $eventTime", Toast.LENGTH_LONG).show()
        }

        preferencesHelper = PreferencesHelper(this)


        val isNotificationEnabled = preferencesHelper.getNotificationSetting()


        if (isNotificationEnabled) {
             startDailyNotification()
        }
               val themePreference = ThemePreference.getInstance(applicationContext.dataStore)

            lifecycleScope.launch {
            themePreference.getThemeSetting().collect { isDarkModeActive ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }


        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            bottomNavigationView.selectedItemId = R.id.nav_home
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.nav_settings -> SettingsFragment()
                R.id.nav_favorite -> FavoriteFragment()
                R.id.nav_upcoming -> UpcomingFragment()
                R.id.nav_past -> PastFragment()
                R.id.nav_home -> HomeFragment()
                else -> null
            }
            selectedFragment?.let {
                loadFragment(it)
            }
            true
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
                when (fragment) {
                    is PastFragment -> {
                        loadFragment(UpcomingFragment())
                        bottomNavigationView.selectedItemId = R.id.nav_upcoming
                    }

                    is UpcomingFragment -> {
                        loadFragment(HomeFragment())
                        bottomNavigationView.selectedItemId = R.id.nav_home
                    }

                    else -> finish()
                }
            }
        })
    }

    private fun startDailyNotification() {
        val dailyWorkRequest: WorkRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
                .build()
        WorkManager.getInstance(this).enqueue(dailyWorkRequest)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
