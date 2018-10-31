package io.tipblockchain.kasakasa.ui.mainapp.transactions

import android.arch.lifecycle.LifecycleOwner
import io.tipblockchain.kasakasa.data.db.entity.Transaction
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView
import java.math.BigDecimal

interface WalletInterface {
    interface View: BaseView, LifecycleOwner {
        fun onBalanceFetched(address: String, currency: Currency, balance: BigDecimal)
        fun onTransactionsFetched(address: String, currency: Currency, transactions: List<Transaction>)
    }

    interface Presenter: BasePresenter<View> {
        fun fetchBalance(address: String?, currency: Currency)
        fun getTransactions(address: String?, currency: Currency)
        fun switchCurrency(currency: Currency)
    }
}