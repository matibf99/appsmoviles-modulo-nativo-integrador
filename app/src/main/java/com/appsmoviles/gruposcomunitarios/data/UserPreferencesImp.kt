package com.appsmoviles.gruposcomunitarios.data

import android.content.SharedPreferences
import com.appsmoviles.gruposcomunitarios.domain.repository.UserPreferences

class UserPreferencesImp(
    private val sharedPreferences: SharedPreferences
) : UserPreferences {
    override fun setUsername(username: String) {
        val editor = sharedPreferences.edit()
        editor.putString(USERNAME, username)
        editor.apply()
    }

    override fun getUsername(): String? {
        return sharedPreferences.getString(USERNAME, null)
    }

    override fun setLanguage(language: String) {
        val editor = sharedPreferences.edit()
        editor.putString(LANGUAGE, language)
        editor.apply()
    }

    override fun getLanguage(): String? {
        return sharedPreferences.getString(LANGUAGE, null)
    }

    companion object {
        const val USERNAME = "username"
        const val LANGUAGE = "language"
    }
}