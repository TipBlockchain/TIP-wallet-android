package io.tipblockchain.kasakasa.ui.splash

import android.app.Application
import io.tipblockchain.kasakasa.data.db.repository.UserRepository
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository
import io.tipblockchain.kasakasa.ui.BasePresenter

class SplashPresenter: BasePresenter {
    var walletRepository: WalletRepository
    var view: SplashView? = null

    constructor(application: Application, view: SplashView) {
        walletRepository = WalletRepository(application)
        this.view = view
    }

    override fun onResume() {
        checkForUserAndWallet()
    }

    override fun onDestroy() {
    }

    private fun checkForUserAndWallet() {
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