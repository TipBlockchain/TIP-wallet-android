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
        private val PLACEHOLDER_VALUE = "playceholder_value"


        var onboardingComplete: Boolean
            get() = prefs.getBoolean(ONBOARDING_COMPLETE, false)
            set(value) = prefs.edit().putBoolean(ONBOARDING_COMPLETE, value).apply()

        var placehoderValue: String
            get() = prefs.getString(PLACEHOLDER_VALUE, "")
            set(value) = prefs.edit().putString(PLACEHOLDER_VALUE, value).apply()

        var currentUser: String?
            get() = prefs.getString(CURRENT_USER, null)
            set(value) {
                if (value != null) {
                    prefs.edit().putString(CURRENT_USER, value).apply()
                } else {
                    this.removeCurrentUser()
                }

            }

        private fun removeCurrentUser() {
            prefs.edit().remove(CURRENT_USER)
        }
    }

}