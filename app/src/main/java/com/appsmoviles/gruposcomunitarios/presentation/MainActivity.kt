package com.appsmoviles.gruposcomunitarios.presentation

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.appsmoviles.gruposcomunitarios.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

import androidx.navigation.fragment.NavHostFragment
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.utils.locale.LocaleHelper


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

        val navHost = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navHost.navController)
    }

    fun displayHomeButton(state: Boolean) {
        supportActionBar!!.setDisplayHomeAsUpEnabled(state)
        supportActionBar!!.setDisplayShowHomeEnabled(state)
    }
}