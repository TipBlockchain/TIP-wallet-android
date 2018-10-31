package io.tipblockchain.kasakasa.ui.mainapp.transactions

import android.arch.lifecycle.Observer
import io.tipblockchain.kasakasa.crypto.EthProcessor
import io.tipblockchain.kasakasa.crypto.TipProcessor
import io.tipblockchain.kasakasa.crypto.TransactionProcessor
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository

class WalletPresenter: WalletInterface.Presenter {

    private var tipProcessor: TipProcessor? = null
    private var ethProcessor = EthProcessor()
    private var currentProcessor: TransactionProcessor? = null
    private var currency: Currency = Currency.TIP

    init {
        currentProcessor = tipProcessor
    }

    override fun attach(view: WalletInterface.View) {
        super.attach(view)
        startListening()
    }

    override fun detach() {
        stopListening()
        super.detach()
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
        this.currency = currency
        when (currency) {
            Currency.ETH -> currentProcessor = ethProcessor
            Currency.TIP -> currentProcessor = tipProcessor
        }
        loadWallet()
    }

    override var view: WalletInterface.View? = null

    private fun startListening() {

    }

    private fun stopListening() {

    }

    private fun loadWallet() {
        WalletRepository.instance.findWalletForCurrency(currency).observe(view!!, Observer { wallet ->
            if (wallet != null) {
                if (currency == Currency.TIP && tipProcessor == null) {
                    tipProcessor = TipProcessor(wallet)
                }
                fetchBalance(wallet.address, currency)
            }
        })
    }
}