package com.appsmoviles.gruposcomunitarios.utils.locale

import android.content.Context
import android.content.SharedPreferences
import java.util.*

object LocaleManager {

    private const val PREFERENCES_LOCALE = "PREFERENCES_LANGUAGE"
    private const val PREFERENCE_LOCALE = "LOCALE"

    fun getLocale(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_LOCALE, Context.MODE_PRIVATE)
        return sharedPreferences.getString(PREFERENCES_LOCALE, Locale.getDefault().language)!!
    }

    fun setLocale(context: Context, language: String) {
        val editSharedPreferences = context.getSharedPreferences(PREFERENCES_LOCALE, Context.MODE_PRIVATE).edit()
        editSharedPreferences.putString(PREFERENCE_LOCALE, language)
        editSharedPreferences.commit()
    }


}