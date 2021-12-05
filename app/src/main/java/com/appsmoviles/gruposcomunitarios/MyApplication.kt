package com.appsmoviles.gruposcomunitarios

import android.app.Application
import android.content.Context
import com.appsmoviles.gruposcomunitarios.utils.locale.LocaleHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }
}