package io.tipblockchain.kasakasa.ui.splash

import android.util.Log
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.data.db.repository.UserRepository

class SplashPresenter: SplashScreenContract.Presenter {

    override var view: SplashScreenContract.View? = null
    private var currentUser: User? = null
    val LOG_TAG = javaClass.canonicalName

    override fun attach(view: SplashScreenContract.View) {
        this.view = view
        checkForUserAndWallet()
    }

    override fun detach() {
        view = null
    }

    override fun checkForUserAndWallet() {
        currentUser = UserRepository.currentUser
    }

    override fun walletFetched(wallet: Wallet?) {
        Log.d(LOG_TAG, "CurrentUser=$currentUser, address=${currentUser?.address ?: "null"}, wallet = $wallet, ")
        if (currentUser == null || wallet == null || currentUser?.address != wallet.address) {
            view?.gotoOnboarding()
        } else {
            view?.gotoMainApp()
        }
    }
}