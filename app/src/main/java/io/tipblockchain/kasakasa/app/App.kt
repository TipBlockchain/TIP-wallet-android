package io.tipblockchain.kasakasa.app

import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.multidex.MultiDex
import android.support.v4.content.LocalBroadcastManager
import com.facebook.stetho.Stetho
import io.tipblockchain.kasakasa.data.db.TipRoomDatabase
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import io.tipblockchain.kasakasa.config.AppProperties

class App : Application() {

    lateinit var preferenceHelper: PreferenceHelper
    private val LOG_TAG = javaClass.name

    companion object {
        lateinit var instance: App
        var configLoaded: Boolean = false
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
    }

    fun fetchProperties() {
        AppProperties.fetchProperties { success ->
            this.configLoadedBroadcast(success)
            configLoaded = true
        }
    }

    private fun configLoadedBroadcast(success: Boolean) {
        val intent = Intent()
        intent.action = AppConstants.ACTION_CONFIG_LOADED
        intent.putExtra(AppConstants.EXTRA_CONFIG_LOADED, success)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}