package io.tipblockchain.kasakasa.ui.onboarding.profile

import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.data.responses.Authorization
import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView
import java.io.File

interface OnboardingUserProfile {

    interface Presenter: BasePresenter <View> {
        var viewModel: OnboardingUserProfileViewModel
        var wallet: Wallet?

        fun checkUsername(username: String)
        fun createAccount()
        fun uploadPhoto(imageFile: File)
    }

    interface View: BaseView {
        fun onPhotoUploaded()
        fun onErrorUpdatingUser(error: Throwable)
        fun onWalletNotSetupError()
        fun onGenericError(error: Throwable)
        fun onInvalidUser()
        fun onUsernameAvailable()
        fun onUsernameUnavailableError()
        fun onAuthorizationFetched(auth: Authorization?, error: Throwable?)
    }
}