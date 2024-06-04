package eu.jw.tbo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import eu.jw.tbo.util.SharedPreferencesManager

@HiltAndroidApp
class TboApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        SharedPreferencesManager.init(this)
    }
}