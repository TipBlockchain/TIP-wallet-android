package io.tipblockchain.kasakasa.ui.onboarding.password

import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository
import java.lang.Error

class ChoosePasswordPresenter: ChoosePassword.Presenter {

    private var walletRepository = WalletRepository.instance
    private var existingUser: User? = null

    override fun setExistingUser(user: User) {
        existingUser = user
    }

    override fun generateWalletFromMnemonicAndPassword(mnemonic: String, password: String) {
        try {
            val newWallet = walletRepository.newWalletWithMnemonicAndPassword(mnemonic = mnemonic, password = password)
            if (newWallet != null) {
                if (existingUser != null) {
                    if (existingUser!!.address != newWallet!!.wallet.address) {
                        view?.onWalletNotMatchingExistingError()
                    } else {
                        view?.onWalletRestored()
                    }
                } else {
                    view?.onWalletCreated()
                }
            } else {
                view?.onWalletCreationError(Error())
            }
        } catch (err: Throwable) {
           view?.onWalletCreationError(err)
        }
    }

    override var view: ChoosePassword.View? = null
}