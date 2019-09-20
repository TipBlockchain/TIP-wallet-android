package io.tipblockchain.kasakasa.ui.mainapp.walletlist

import android.arch.lifecycle.LifecycleOwner
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView

interface WalletList {
    interface  View: BaseView, LifecycleOwner {
        fun showWallets(wallets: List<Wallet>)
    }

    interface Presenter: BasePresenter<View> {
        fun loadWallets()
    }
}