package io.tipblockchain.kasakasa.ui.onboarding.profile

import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView

interface OnboardingUserProfile {

    interface Presenter: BasePresenter <View> {
        var viewModel: OnboardingUserProfileViewModel
        var wallet: Wallet?

        fun checkUsername(username: String)
        fun createAccount()
    }

    interface View: BaseView {
        fun onWalletNotSetupError()
        fun onGenericError(error: Throwable)
        fun onInvalidUser()
        fun onUsernameUnavailableError()
        fun onAccountCreated()
    }
}