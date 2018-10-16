package io.tipblockchain.kasakasa.ui.splash

import android.app.Application
import android.util.Log
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.data.db.repository.UserRepository
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository

class SplashPresenter: SplashScreenContract.Presenter {

    var walletRepository: WalletRepository
    override var view: SplashScreenContract.View? = null
    private var currentUser: User? = null
    val LOG_TAG = javaClass.canonicalName

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
        currentUser = UserRepository.currentUser
        if (currentUser == null) {
            Log.d(LOG_TAG, "currentUser is null -> Onboarding")
            view?.gotoOnboarding()
        } else {
            Log.d(LOG_TAG, "CurrentUser is NOT null... Continuing")
        }
    }

    override fun walletFetched(wallet: Wallet?) {
        if (currentUser == null || wallet == null || currentUser?.address != wallet.address) {
            view?.gotoOnboarding()
            Log.d(LOG_TAG, "Wallet is null -> Onboarding")
        } else {
            view?.gotoMainApp()
            Log.d(LOG_TAG, "Wallet is NOT null -> Main App")
        }
    }
}