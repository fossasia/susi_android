package org.fossasia.susi.ai

import android.annotation.SuppressLint
import android.app.Application
import android.support.v7.app.AppCompatDelegate
import android.util.Log
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import io.realm.Realm
import io.realm.RealmConfiguration
import org.fossasia.susi.ai.di.modules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class MainApplication : Application() {

    companion object {
        lateinit var instance: MainApplication
            private set
        private val TAG = "MainApplication"

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(listOf(modules))
        }

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)

        instance = this
        // The Realm file will be located in Context.getFilesDir() with name "default.realm"
        Realm.init(this)
        val config = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build()
        Realm.setDefaultConfiguration(config)

        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                // Add the line number to the tag
                override fun createStackElementTag(element: StackTraceElement): String? {
                    return super.createStackElementTag(element) + ": " + element.lineNumber
                }
            })
        } else {
            // Release mode
            Timber.plant(ReleaseLogTree())
        }

        Stetho.initializeWithDefaults(this)
    }

    private class ReleaseLogTree : Timber.Tree() {

        @SuppressLint("LogNotTimber") // Prevent Recursive Access
        override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
            if (priority == Log.DEBUG || priority == Log.VERBOSE || priority == Log.INFO) {
                return
            }

            if (priority == Log.ERROR) {
                if (throwable == null) {
                    Log.e(TAG, message)
                } else {
                    Log.e(TAG, message + "\n" + throwable.toString())
                }
            }
        }
    }
}
