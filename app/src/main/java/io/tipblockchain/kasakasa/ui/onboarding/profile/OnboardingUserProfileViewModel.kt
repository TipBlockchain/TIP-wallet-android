package io.tipblockchain.kasakasa.ui.onboarding.profile

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.graphics.drawable.BitmapDrawable
import android.util.Log

class OnboardingUserProfileViewModel: AndroidViewModel {

    var profilePhoto: BitmapDrawable? = null
    var username: String? = null
    var firstname: String? = null
    var lastname: String? = null

    constructor(application: Application): super(application) {

    }

    fun canProceed(): Boolean {
        Log.d("Debug", this.toString())
        return username != null && firstname != null && lastname != null
    }

    override fun toString(): String {
        return "{username: $username, firstname: $firstname, lastname: $lastname, photo: $profilePhoto}"
    }
}