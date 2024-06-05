package eu.jw.tbo

import android.app.Application
import android.content.Context
import android.os.StrictMode
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication
import eu.jw.tbo.util.SharedPreferencesManager

class HiltTestRunner: AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        if (context != null) {
            SharedPreferencesManager.init(context)
        }
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}