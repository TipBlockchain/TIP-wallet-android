package io.tipblockchain.kasakasa.app

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.facebook.stetho.Stetho
import io.tipblockchain.kasakasa.data.db.TipRoomDatabase


class App : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: App? = null
        lateinit var preferences: Preferences
        private set

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }

        fun application(): App {
            return instance!!
        }
    }

    override fun onCreate() {
        super.onCreate()

        MultiDex.install(this)
        preferences = Preferences(this)

        Stetho.initializeWithDefaults(this)
        TipRoomDatabase.getDatabase(instance!!.applicationContext)
        Preferences.placehoderValue = "Some random value"

        // Use ApplicationContext.
        // example: SharedPreferences etc...
    }
}