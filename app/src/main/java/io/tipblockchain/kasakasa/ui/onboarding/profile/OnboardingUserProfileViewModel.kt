package io.tipblockchain.kasakasa.ui.onboarding.profile

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.graphics.drawable.BitmapDrawable
import android.text.TextUtils
import io.tipblockchain.kasakasa.data.db.repository.UserRepository
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository

class OnboardingUserProfileViewModel: AndroidViewModel {

    var profilePhoto: BitmapDrawable? = null
    var username: String? = null
    var firstname: String? = null
    var lastname: String? = null

    var userRepository: UserRepository = UserRepository.instance
    var walletRepository: WalletRepository = WalletRepository.instance


    var name: String = ""
        get() {
            var name = ""
            if (!TextUtils.isEmpty(firstname)) {
                name += firstname
            }
            if (!TextUtils.isEmpty(lastname)) {
                if (!TextUtils.isEmpty(name)) {
                    name += " "
                }
                name += lastname
            }
            return name
        }

    constructor(application: Application): super(application) {
    }

    override fun toString(): String {
        return "{username: $username, firstname: $firstname, lastname: $lastname, photo: $profilePhoto}"
    }

    fun destroy() {
    }
}