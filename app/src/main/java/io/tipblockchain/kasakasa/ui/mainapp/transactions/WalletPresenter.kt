package io.tipblockchain.kasakasa.ui.mainapp.transactions

import android.arch.lifecycle.Observer
import io.tipblockchain.kasakasa.blockchain.eth.Web3Bridge
import io.tipblockchain.kasakasa.crypto.EthProcessor
import io.tipblockchain.kasakasa.crypto.TipProcessor
import io.tipblockchain.kasakasa.crypto.TransactionProcessor
import io.tipblockchain.kasakasa.data.db.entity.Wallet
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

    override fun fetchBalance(wallet: Wallet) {
        val balance = currentProcessor?.getBalance(wallet.address) ?: BigInteger.ZERO
        val balanceInEth =  Convert.fromWei(balance.toBigDecimal(), Convert.Unit.ETHER)
        val latestBlock = Web3Bridge().latestBlock()
        if (balance != null) {
            if (balance != wallet.balance || latestBlock != wallet.blockNumber) {
                wallet.balance = balance
                wallet.blockNumber = latestBlock
                walletRepository.update(wallet)
            }
            view?.onBalanceFetched(wallet.address, Currency.valueOf(wallet.currency), balanceInEth)
         } else {
             view?.onBalanceFetchError()
         }
    }

    override fun getTransactions(wallet: Wallet) {
        val latestBlock = Web3Bridge().latestBlock()
        if (latestBlock == wallet.blockNumber) {
            return
        }
        val c: Currency = Currency.valueOf(wallet.currency)
        when (c) {
            Currency.TIP -> {
                txRepository.fetchTipTransactions(address = wallet.address, startBlock = wallet.blockNumber.toString(), callback = { txlist, err ->
                    if (txlist != null) {
                        if (txlist.count() > 0) {
                        }
                        view?.onTransactionsFetched(wallet.address, Currency.TIP, txlist)
                    } else {
                        view?.onTransactionsFetchError(err, Currency.TIP)
                    }
                })
            }
            Currency.ETH -> {
                txRepository.fetchEthTransactions(address = wallet.address, startBlock = wallet.blockNumber.toString(), callback = { txlist, err ->
                    if (txlist != null) {
                        view?.onTransactionsFetched(wallet.address, Currency.ETH, txlist)
                    } else {
                        view?.onTransactionsFetchError(err, Currency.ETH)
                    }
                })
            }
        }
    }

    override fun switchCurrency(currency: Currency) {
        when (currency) {
            Currency.TIP -> currentProcessor = tipProcessor
            Currency.ETH -> currentProcessor = ethProcessor
        }
        loadWallet(currency)
    }

    override var view: WalletInterface.View? = null

    private fun startListening() {

    }

    private fun stopListening() {

    }

    private fun loadWallet(currency: Currency) {
        if (view != null) {
            walletRepository.findWalletForCurrency(currency).observe(view!!, Observer { wallet ->
                if (wallet != null) {
                    if (currency == Currency.TIP && tipProcessor == null) {
                        tipProcessor = TipProcessor(wallet)
                    }
                    getTransactions(wallet)
                    fetchBalance(wallet)
                }
            })
        }
    }
}