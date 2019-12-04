package io.tipblockchain.kasakasa.ui.mainapp.walletlist

import android.arch.lifecycle.LifecycleOwner
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView
import java.math.BigDecimal

interface WalletList {
    interface  View: BaseView, LifecycleOwner {
        fun showWallets(wallets: List<Wallet>)
        fun onBalanceFetched(address: String, currency: Currency, balance: BigDecimal)
        fun onBalanceFetchError(address: String, currency: Currency)
    }

    interface Presenter: BasePresenter<View> {
        fun loadWallets()
    }
}