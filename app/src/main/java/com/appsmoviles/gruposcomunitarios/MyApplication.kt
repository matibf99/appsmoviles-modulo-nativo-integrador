package com.appsmoviles.gruposcomunitarios

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.appsmoviles.gruposcomunitarios.utils.locale.LocaleHelper
import com.appsmoviles.gruposcomunitarios.utils.notification.NotificationUtils
import com.appsmoviles.gruposcomunitarios.utils.notification.UpdatedPostHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }

    override fun onCreate() {
        NotificationUtils.createNotificationChannel(applicationContext)
        UpdatedPostHelper.removeOldPosts(applicationContext)
        super.onCreate()
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}