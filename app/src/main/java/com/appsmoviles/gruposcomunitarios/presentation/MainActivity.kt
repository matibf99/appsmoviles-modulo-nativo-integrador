package com.appsmoviles.gruposcomunitarios.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.ui.NavigationUI
import com.appsmoviles.gruposcomunitarios.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

import androidx.navigation.fragment.NavHostFragment
import com.appsmoviles.gruposcomunitarios.R


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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