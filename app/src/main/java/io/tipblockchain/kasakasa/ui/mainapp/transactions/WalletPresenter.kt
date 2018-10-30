package io.tipblockchain.kasakasa.ui.mainapp.transactions

import io.tipblockchain.kasakasa.crypto.EthProcessor
import io.tipblockchain.kasakasa.crypto.TipProcessor
import io.tipblockchain.kasakasa.crypto.TransactionProcessor
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.data.db.repository.Currency

class WalletPresenter: WalletInterface.Presenter {

    private var tipProcessor: TipProcessor? = null
    private var ethProcessor = EthProcessor()
    private var currentProcessor: TransactionProcessor? = null

    init {
        currentProcessor = ethProcessor
    }

    override fun attach(view: WalletInterface.View) {
        super.attach(view)
        startListening()
    }

    override fun detach() {
        stopListening()
        super.detach()
    }

    override fun setWallet(wallet: Wallet) {
        tipProcessor = TipProcessor(wallet)
    }

    override fun fetchBalance(address: String?, currency: Currency) {
        if (address != null) {
            val balance = currentProcessor?.getBalance(address)
            if (balance != null) {
                view?.onBalanceFetched(address, currency, balance)
            }
        }
    }

    override fun getTransactions(address: String?, currency: Currency) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun switchCurrency(currency: Currency) {
        when (currency) {
            Currency.ETH -> currentProcessor = ethProcessor
            Currency.TIP -> currentProcessor = tipProcessor
        }
    }

    override var view: WalletInterface.View? = null

    private fun startListening() {

    }

    private fun stopListening() {

    }
}