package io.tipblockchain.kasakasa.ui.splash

import android.app.Application
import io.tipblockchain.kasakasa.data.db.repository.UserRepository
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository

class SplashPresenter: SplashScreenContract.Presenter {

    var walletRepository: WalletRepository
    var view: SplashScreenContract.View? = null

    constructor(application: Application) {
        walletRepository = WalletRepository(application)
    }

    override fun attach(view: SplashScreenContract.View) {
        this.view = view
        checkForUserAndWallet()
    }

    override fun detach() {
        view = null
    }

    override fun checkForUserAndWallet() {
        val currentUser = UserRepository.currentUser
        val primaryWallet = walletRepository.primaryWallet().value

        if (currentUser != null && primaryWallet != null) {
            if (currentUser.address == primaryWallet.address) {
                view?.gotoMainApp()
                return
            }
        }
        view?.gotoOnboarding()
    }
}