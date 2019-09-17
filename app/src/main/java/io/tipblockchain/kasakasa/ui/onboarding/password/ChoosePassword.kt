package io.tipblockchain.kasakasa.ui.onboarding.password

import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView

interface ChoosePassword {

    interface View: BaseView {
        fun onWalletCreated()
        fun onWalletRestored()
        fun onWalletNotMatchingExistingError()
        fun onWalletCreationError(error: Throwable)
    }

    interface Presenter: BasePresenter<View> {
        fun setExistingUser(user: User?)
//        fun generateWalletFromMnemonicAndPassword(mnemonic: String, password: String)
        fun checkAndGenerateWallet(mnemonic: String, password: String)
    }
}