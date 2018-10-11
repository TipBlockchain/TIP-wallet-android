package io.tipblockchain.kasakasa.app

import android.content.Context
import android.content.SharedPreferences

class Preferences (private var context: Context) {

    companion object {

        private var instance: Preferences = Preferences(App.applicationContext())

        private val PREFS_FILENAME = "io.tipblockchain.kasakasa.preferences"
        private var prefs: SharedPreferences = instance.context.getSharedPreferences(PREFS_FILENAME, 0)

        private val ONBOARDING_COMPLETE = "onboarding_complete"
        private val CURRENT_USER = "current_user"


        var onboardingComplete: Boolean
            get() = prefs.getBoolean(ONBOARDING_COMPLETE, false)
            set(value) = prefs.edit().putBoolean(ONBOARDING_COMPLETE, value).apply()

        var currentUser: String?
            get() = prefs.getString(CURRENT_USER, null)
            set(value) = prefs.edit().putString(CURRENT_USER, value).apply()
    }

}