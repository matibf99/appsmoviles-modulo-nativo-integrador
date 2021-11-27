package com.appsmoviles.gruposcomunitarios.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appsmoviles.gruposcomunitarios.databinding.ActivityAuthBinding
import com.appsmoviles.gruposcomunitarios.databinding.ActivityHomeBinding


enum class ProviderType {
    BASIC
}

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}