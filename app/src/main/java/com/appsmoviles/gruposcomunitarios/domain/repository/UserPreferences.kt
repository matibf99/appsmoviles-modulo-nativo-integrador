package com.appsmoviles.gruposcomunitarios.domain.repository

interface UserPreferences {
    fun setUsername(username: String)
    fun getUsername(): String?
    fun setLanguage(language: String)
    fun getLanguage(): String?
}