package com.appsmoviles.gruposcomunitarios.utils.locale

import android.content.Context
import android.content.SharedPreferences
import java.util.*

object LocaleManager {

    private const val PREFERENCES_LOCALE = "PREFERENCES_LANGUAGE"
    private const val PREFERENCE_LOCALE = "LOCALE"

    const val LOCALE_EN = "en"
    const val LOCALE_ES = "es"
    const val LOCALE_ZH = "zh"

    fun getLocale(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_LOCALE, Context.MODE_PRIVATE)
        return sharedPreferences.getString(PREFERENCE_LOCALE, Locale.getDefault().language)!!
    }

    fun setLocale(context: Context, language: String) {
        val editSharedPreferences = context.getSharedPreferences(PREFERENCES_LOCALE, Context.MODE_PRIVATE).edit()
        editSharedPreferences.putString(PREFERENCE_LOCALE, language)
        editSharedPreferences.apply()
    }


}