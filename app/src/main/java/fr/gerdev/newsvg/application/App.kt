package fr.gerdev.newsvg.application

import android.app.Application
import com.facebook.stetho.Stetho
import fr.gerdev.newsvg.BuildConfig
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        //AsyncTask.execute { AppDatabase.getInstance(this).databaseDao().nukeTable() }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            //TODO ADD CRASH REPORTING
            //https://github.com/JakeWharton/timber/blob/master/timber-sample/src/main/java/com/example/timber/ExampleApp.java
        }
    }
}