package io.tipblockchain.kasakasa.app

import android.content.SharedPreferences

class PreferenceHelper {

    companion object {

        var instance: PreferenceHelper = PreferenceHelper()

        private const val PREFS_FILENAME = "io.tipblockchain.kasakasa.preferences"
        private var prefs: SharedPreferences = App.applicationContext().getSharedPreferences(PREFS_FILENAME, 0)

        private const val PENDING_ACCOUNT_TOKEN = "pending_account_token"
        private const val ONBOARDING_COMPLETE = "onboarding_complete"
        private const val CURRENT_USER = "current_user"
        private const val DEMO_ACCOUNT_USER = "demo_account_user"
        private const val PLACEHOLDER_VALUE = "placeholder_value"
        private const val AUTHORIZATION = "authorization"


        var onboardingComplete: Boolean
            get() = prefs.getBoolean(ONBOARDING_COMPLETE, false)
            set(value) = prefs.edit().putBoolean(ONBOARDING_COMPLETE, value).apply()

        var placehoderValue: String?
            get() = prefs.getString(PLACEHOLDER_VALUE, "")
            set(value) = prefs.edit().putString(PLACEHOLDER_VALUE, value).apply()

        var currentUser: String?
            get() = prefs.getString(CURRENT_USER, null)
            set(value) = prefs.edit().putString(CURRENT_USER, value).apply()

        var demoAccountUser: String?
            get() = prefs.getString(DEMO_ACCOUNT_USER, null)
            set(value) = prefs.edit().putString(DEMO_ACCOUNT_USER, value).apply()

        var pendingSignupToken: String?
            get() = prefs.getString(PENDING_ACCOUNT_TOKEN, null)
            set(value) =  prefs.edit().putString(PENDING_ACCOUNT_TOKEN, value).apply()


        var authorization: String?
            get() = prefs.getString(AUTHORIZATION, null)
            set(value) = prefs.edit().putString(AUTHORIZATION, value).apply()

        private fun removeCurrentUser() {
            prefs.edit().remove(CURRENT_USER).apply()
        }
    }
}