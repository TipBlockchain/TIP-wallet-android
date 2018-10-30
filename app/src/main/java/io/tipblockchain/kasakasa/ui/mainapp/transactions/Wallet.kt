package io.tipblockchain.kasakasa.ui.mainapp.transactions

import io.tipblockchain.kasakasa.data.db.entity.Transaction
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView
import java.math.BigDecimal

interface WalletInterface {
    interface View: BaseView {
        fun onBalanceFetched(address: String, currency: Currency, balance: BigDecimal)
        fun onTransactionsFetched(address: String, currency: Currency, transactions: List<Transaction>)
    }

    interface Presenter: BasePresenter<View> {
        fun setWallet(wallet: Wallet)
        fun fetchBalance(address: String?, currency: Currency)
        fun getTransactions(address: String?, currency: Currency)
        fun switchCurrency(currency: Currency)
    }
}