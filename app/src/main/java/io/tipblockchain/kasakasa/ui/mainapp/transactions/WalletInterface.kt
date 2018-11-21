package io.tipblockchain.kasakasa.ui.mainapp.transactions

import android.arch.lifecycle.LifecycleOwner
import io.tipblockchain.kasakasa.data.db.entity.Transaction
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView
import java.math.BigDecimal

interface WalletInterface {

    interface View: BaseView, LifecycleOwner {
        fun onBalanceFetchError()
        fun onBalanceFetched(address: String, currency: Currency, balance: BigDecimal)
        fun onTransactionsFetched(address: String, currency: Currency, transactions: List<Transaction>)
        fun onTransactionsFetchError(error: Throwable?, currency: Currency)
    }

    interface Presenter: BasePresenter<View> {
        fun loadWallets()
        fun fetchBalance(wallet: Wallet): Boolean
        fun fetchTransactions(wallet: Wallet)
        fun switchCurrency(currency: Currency)
    }
}