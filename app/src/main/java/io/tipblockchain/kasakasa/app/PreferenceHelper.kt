package io.tipblockchain.kasakasa.app

import android.content.SharedPreferences

class PreferenceHelper {

    companion object {

        var instance: PreferenceHelper = PreferenceHelper()

        private val PREFS_FILENAME = "io.tipblockchain.kasakasa.preferences"
        private var prefs: SharedPreferences = App.applicationContext().getSharedPreferences(PREFS_FILENAME, 0)

        private val ONBOARDING_COMPLETE = "onboarding_complete"
        private val CURRENT_USER = "current_user"
        private val PLACEHOLDER_VALUE = "playceholder_value"
        private val AUTHORIZATION = "authorization"


        var onboardingComplete: Boolean
            get() = prefs.getBoolean(ONBOARDING_COMPLETE, false)
            set(value) = prefs.edit().putBoolean(ONBOARDING_COMPLETE, value).apply()

        var placehoderValue: String?
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

        var authorization: String?
        get() = prefs.getString(AUTHORIZATION, null)
        set(value) {
            if (value != null) {
                prefs.edit().putString(AUTHORIZATION, value).apply()
            } else {
                prefs.edit().remove(AUTHORIZATION)
            }
        }

        private fun removeCurrentUser() {
            prefs.edit().remove(CURRENT_USER)
        }
    }
}