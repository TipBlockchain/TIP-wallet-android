package io.tipblockchain.kasakasa.app

import android.content.Context
import android.content.SharedPreferences

class Preferences (private var context: Context) {
    private val PREFS_FILENAME = "io.tipblockchain.kasakasa.preferences"

    private val ONBOARDING_COMPLETE = "onboarding_complete"

    private var prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var onboardingComplete: Boolean
        get() = prefs.getBoolean(ONBOARDING_COMPLETE, false)
        set(value) = prefs.edit().putBoolean(ONBOARDING_COMPLETE, value).apply()
}