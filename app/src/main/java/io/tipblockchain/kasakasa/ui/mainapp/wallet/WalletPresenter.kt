package io.tipblockchain.kasakasa.ui.mainapp.wallet

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
import org.web3j.abi.datatypes.Bool
import org.web3j.utils.Convert
import java.math.BigInteger
import java.util.*

class WalletPresenter: WalletInterface.Presenter {

    private var currentProcessor: TransactionProcessor? = null
    private var txRepository: TransactionRepository = TransactionRepository.instance
    private val walletRepository = WalletRepository.instance

    private var currentWallet: Wallet? = null

    private var txDisposable: Disposable? = null

    private val LOG_TAG = javaClass.name

    override fun detach() {
        super.detach()
        txDisposable?.dispose()
    }

    override fun setWallet(wallet: Wallet) {
        currentWallet = wallet
        val currency = Currency.valueOf(wallet.currency)
        val balance = Convert.fromWei(currentWallet!!.balance.toBigDecimal(), Convert.Unit.ETHER)
        view?.onBalanceFetched(currentWallet!!.address, currency = currency, balance = balance)
        when (currency) {
            Currency.TIP -> {
                currentProcessor = TipProcessor(wallet = wallet)
            }
            Currency.ETH -> {
                currentProcessor = EthProcessor()
            }
        }
        fetchBalance(wallet)
        loadTransactions(wallet)
        fetchTransactions(wallet)
    }

    override fun fetchBalance(wallet: Wallet): Boolean {
        var balanceChanged = false
        try {
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

        return balanceChanged
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
                .subscribe ({ txlist ->
            if (currentWallet != null) {
                val address = currentWallet!!.address
                val currentWalletTxList = txlist.filter { tx ->
                    /* tx.currency == currentWallet?.currency && */ (tx.from == address || tx.to == address)
                }
                view?.onTransactionsFetched(address, Currency.valueOf(currentWallet!!.currency), currentWalletTxList)
            }
        }, {
                    view?.onTransactionsFetchError(it, Currency.valueOf(wallet.currency))
                })
    }
}