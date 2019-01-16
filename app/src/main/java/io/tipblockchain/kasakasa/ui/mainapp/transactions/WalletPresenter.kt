package io.tipblockchain.kasakasa.ui.mainapp.transactions

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
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
import java.util.*

class WalletPresenter: WalletInterface.Presenter {

    private var tipProcessor: TipProcessor? = null
    private var ethProcessor = EthProcessor()
    private var currentProcessor: TransactionProcessor? = null
    private var txRepository: TransactionRepository = TransactionRepository.instance
    private val walletRepository = WalletRepository.instance

    private var tipWallet: Wallet? = null
    private var ethWallet: Wallet? = null
    private var currentWallet: Wallet? = null

    private var txDisposable: Disposable? = null
    private var txFound = false

    private val LOG_TAG = javaClass.name
    init {
        currentProcessor = tipProcessor
    }

    override fun detach() {
        stopListening()
        super.detach()
    }

    override fun switchCurrency(currency: Currency) {
        when (currency) {
            Currency.TIP -> {
                currentProcessor = tipProcessor
                currentWallet = tipWallet
            }
            Currency.ETH -> {
                currentProcessor = ethProcessor
                currentWallet = ethWallet
            }
        }
        if (currentWallet != null) {
            val balance = Convert.fromWei(currentWallet!!.balance.toBigDecimal(), Convert.Unit.ETHER)
            view?.onBalanceFetched(currentWallet!!.address, currency = currency, balance = balance)
            loadTransactions(wallet = currentWallet!!)
            val balanceChanged = fetchBalance(currentWallet!!)
//            if (balanceChanged) {
//                fetchTransactions(currentWallet!!)
//            }
            fetchTransactions(currentWallet!!)
        }
    }

    override fun fetchBalance(wallet: Wallet): Boolean {
        try {
            var balanceChanged = false
            val balance = currentProcessor?.getBalance(wallet.address) ?: BigInteger.ZERO
            val balanceInEth =  Convert.fromWei(balance.toBigDecimal(), Convert.Unit.ETHER)
            if (balance != null) {
                if (balance != wallet.balance) {
                    wallet.balance = balance
                    wallet.lastSynced = Date()
                    walletRepository.update(wallet)
                    balanceChanged = true
                    Log.d(LOG_TAG, "Balance has changed")
                } else {
                    Log.d(LOG_TAG, "Balance still the same")
                }
                view?.onBalanceFetched(wallet.address, Currency.valueOf(wallet.currency), balanceInEth)
                return balanceChanged
            } else {
                view?.onBalanceFetchError()
            }
        } catch (err: Throwable) {
            view?.onBalanceFetchError()
        }

        return false
    }

    override fun fetchTransactions(wallet: Wallet) {
        val latestBlock = Web3Bridge().latestBlock()
        val c: Currency = Currency.valueOf(wallet.currency)
        when (c) {
            Currency.TIP -> {
                txRepository.fetchTipTransactions(address = wallet.address, startBlock = wallet.startBlockNumber.toString(), endBlock = "latest", callback = { txlist, err ->
                    wallet.blockNumber = latestBlock
                    walletRepository.update(wallet)
                    if (txlist == null) {
                        view?.onTransactionsFetchError(err, Currency.TIP)
                    }
                })
            }
            Currency.ETH -> {
                txRepository.fetchEthTransactions(address = wallet.address, startBlock = wallet.startBlockNumber.toString(), endBlock = "latest", callback = { txlist, err ->
                    this.loadTransactions(wallet)
                    if (txlist == null) {
                        view?.onTransactionsFetchError(err, Currency.ETH)
                    }
                })
            }
        }
        if (wallet.blockNumber != latestBlock) {
            wallet.blockNumber = latestBlock.minus(BigInteger.valueOf(7)).max(wallet.blockNumber)
            walletRepository.update(wallet)
        }
        this.loadTransactions(wallet)
    }

    override var view: WalletInterface.View? = null

    private fun startListening() {
        txRepository.loadAllTransactions().observe(view!!, Observer { txlist ->
            if (currentWallet != null && txlist != null && !txlist.isEmpty()) {
                val address = currentWallet!!.address
                val currentWalletTxList = txlist.filter { tx ->
                    tx.currency == currentWallet?.currency && (tx.from == address || tx.to == address)
                }
                view?.onTransactionsFetched(address, Currency.valueOf(currentWallet!!.currency), currentWalletTxList)
            }
        })
    }

    private fun loadTransactions(wallet: Wallet) {
        val currency = Currency.valueOf(wallet.currency)
        txDisposable?.dispose()

        txDisposable = txRepository.loadTransactions_notLive(currency)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { txlist ->
            if (currentWallet != null) {
                val address = currentWallet!!.address
                val currentWalletTxList = txlist.filter { tx ->
                    /* tx.currency == currentWallet?.currency && */ (tx.from == address || tx.to == address)
                }
                view?.onTransactionsFetched(address, Currency.valueOf(currentWallet!!.currency), currentWalletTxList)
            }
        }
    }

    private fun stopListening() {

    }

    override fun loadWallets() {
        if (ethWallet == null) {
            walletRepository.findWalletForCurrency(Currency.ETH).observe(view!!, Observer { wallet ->
                if (wallet != null) {
                    ethWallet = wallet
                    Log.d(LOG_TAG, "Updating ETH wallet on change")
                    view?.onBalanceFetched(wallet.address, Currency.valueOf(wallet.currency), Convert.fromWei(wallet.balance.toBigDecimal(), Convert.Unit.ETHER))
                }
            })
        }
        if (tipWallet == null) {
            walletRepository.findWalletForCurrency(Currency.TIP).observe(view!!, Observer { wallet ->
                if (wallet != null && tipWallet == null) {
                    Log.d(LOG_TAG, "Updating TIP wallet on change")
                    view?.onBalanceFetched(wallet.address, Currency.valueOf(wallet.currency), Convert.fromWei(wallet.balance.toBigDecimal(), Convert.Unit.ETHER))
                    tipWallet = wallet
                    if(tipProcessor == null) {
                        tipProcessor = TipProcessor(wallet)
                    }
                }
            })
        }
        startListening()
    }
}