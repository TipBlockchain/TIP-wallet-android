package io.tipblockchain.kasakasa.ui.mainapp.transactions

import android.arch.lifecycle.Observer
import io.tipblockchain.kasakasa.crypto.EthProcessor
import io.tipblockchain.kasakasa.crypto.TipProcessor
import io.tipblockchain.kasakasa.crypto.TransactionProcessor
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.data.db.repository.TransactionRepository
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository
import org.web3j.utils.Convert
import java.math.BigInteger

class WalletPresenter: WalletInterface.Presenter {

    private var tipProcessor: TipProcessor? = null
    private var ethProcessor = EthProcessor()
    private var currentProcessor: TransactionProcessor? = null
    private var txRepository: TransactionRepository = TransactionRepository.instance
    private val walletRepository = WalletRepository.instance
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
            val balance = currentProcessor?.getBalance(address) ?: BigInteger.ZERO
            val balanceInEth =  Convert.fromWei(balance.toBigDecimal(), Convert.Unit.ETHER)
            WalletRepository.instance.findWalletForAddressAndCurrency(address, currency).observe(view!!, Observer {
               if (it != null && balance != null) {
                   it.balance = balance
                   walletRepository.update(it)
                   view?.onBalanceFetched(address, currency, balanceInEth)
               } else {
                   view?.onBalanceFetchError()
               }
            })
        }
    }

    override fun getTransactions(address: String, startBlock: String, currency: Currency) {
        when (currency) {
            Currency.TIP -> {
                txRepository.fetchTipTransactions(address = address, startBlock = startBlock, callback = { txlist, err ->
                    if (txlist != null) {
                        view?.onTransactionsFetched(address, Currency.TIP, txlist)
                    } else {
                        view?.onTransactionsFetchError(err, Currency.TIP)
                    }
                })
            }
            Currency.ETH -> {
                txRepository.fetchEthTransactions(address = address, startBlock = startBlock, callback = { txlist, err ->
                    if (txlist != null) {
                        view?.onTransactionsFetched(address, Currency.ETH, txlist)
                    } else {
                        view?.onTransactionsFetchError(err, Currency.ETH)
                    }
                })
            }
        }
    }

    override fun switchCurrency(currency: Currency) {
        this.currency = currency
        when (currency) {
            Currency.TIP -> currentProcessor = tipProcessor
            Currency.ETH -> currentProcessor = ethProcessor
        }
        loadWallet()
    }

    override var view: WalletInterface.View? = null

    private fun startListening() {

    }

    private fun stopListening() {

    }

    private fun loadWallet() {
        if (view != null) {
            WalletRepository.instance.findWalletForCurrency(currency).observe(view!!, Observer { wallet ->
                if (wallet != null) {
                    if (currency == Currency.TIP && tipProcessor == null) {
                        tipProcessor = TipProcessor(wallet)
                    }
                    fetchBalance(wallet.address, currency)
                    getTransactions(address = wallet.address, currency = currency, startBlock = wallet.blockNumber)
                }
            })
        }
    }
}