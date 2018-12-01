package io.tipblockchain.kasakasa.app

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.facebook.stetho.Stetho
import io.tipblockchain.kasakasa.data.db.TipRoomDatabase
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

class App : Application() {

    lateinit var preferenceHelper: PreferenceHelper

    companion object {
        lateinit var instance: App
        private set

        fun applicationContext() : Context {
            return instance.applicationContext
        }

        fun application(): App {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        MultiDex.install(this)

        Stetho.initializeWithDefaults(this)
        TipRoomDatabase.getDatabase(instance.applicationContext)
        PreferenceHelper.placehoderValue = "Some random value"
        Fabric.with(this, Crashlytics())
        // Use ApplicationContext.
        // example: SharedPreferences etc...
    }
}