package io.tipblockchain.kasakasa.ui.splash

import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView

interface SplashScreenContract {
    interface Presenter: BasePresenter<View> {
        fun walletFetched(wallet: Wallet?)
        fun checkForUserAndWallet()
    }

    interface View: BaseView {
        fun gotoOnboarding()
        fun gotoMainApp()
    }
}