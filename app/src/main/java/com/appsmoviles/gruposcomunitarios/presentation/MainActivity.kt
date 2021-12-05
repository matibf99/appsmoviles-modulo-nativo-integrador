package com.appsmoviles.gruposcomunitarios.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.ActivityMainBinding
import com.appsmoviles.gruposcomunitarios.utils.locale.LocaleHelper
import com.appsmoviles.gruposcomunitarios.worker.NotificationWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel: MainAcitivityViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.topAppBar)
        startWorker()

        val navHost = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navHost.navController)
    }

    private fun startWorker() {
        val worker: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
                .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork("Notifications", ExistingPeriodicWorkPolicy.KEEP, worker)
    }

    fun displayHomeButton(state: Boolean) {
        supportActionBar!!.setDisplayHomeAsUpEnabled(state)
        supportActionBar!!.setDisplayShowHomeEnabled(state)
    }
}