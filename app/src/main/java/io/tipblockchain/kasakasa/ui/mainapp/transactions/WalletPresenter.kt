package io.tipblockchain.kasakasa.ui.mainapp.transactions

import io.tipblockchain.kasakasa.crypto.EthProcessor
import io.tipblockchain.kasakasa.crypto.TipProcessor
import io.tipblockchain.kasakasa.crypto.TransactionProcessor
import io.tipblockchain.kasakasa.data.db.repository.Currency

class WalletPresenter: Wallet.Presenter {

    private val tipProcessor = TipProcessor()
    private val ethProcessor = EthProcessor()
    private var currentProcessor: TransactionProcessor? = null

    override fun attach(view: Wallet.View) {
        super.attach(view)
        startListening()
    }

    override fun detach() {
        stopListening()
        super.detach()
    }
    override fun fetchBalance(address: String, currency: Currency) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTransactions(address: String, currency: Currency) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun switchCurrency(currency: Currency) {
        when (currency) {
            Currency.ETH -> currentProcessor = ethProcessor
            Currency.TIP -> currentProcessor = tipProcessor
        }
        currentProcessor?.getBalance("")
    }

    override var view: Wallet.View?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    private fun startListening() {

    }

    private fun stopListening() {

    }
}