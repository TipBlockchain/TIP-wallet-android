package io.tipblockchain.kasakasa.data.db.repository

import android.arch.lifecycle.LiveData
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.app.App
import io.tipblockchain.kasakasa.blockchain.eth.Web3Bridge
import io.tipblockchain.kasakasa.data.db.TipRoomDatabase
import io.tipblockchain.kasakasa.data.db.entity.Transaction
import io.tipblockchain.kasakasa.data.db.dao.TransactionDao
import io.tipblockchain.kasakasa.networking.EtherscanApiService
import java.math.BigInteger

enum class Currency {
    TIP,
    ETH
}

typealias TransactionsUpdatedWithResults = (List<Transaction>?, Throwable?) -> Unit

class TransactionRepository {

    private var dao: TransactionDao
    private var allTransactions: LiveData<List<Transaction>>
    private var etherscanApiService = EtherscanApiService.instance

    private var tipTxDisposable: Disposable? = null
    private var ethTxDisposable: Disposable? = null

    private constructor(context: Context) {
        val db = TipRoomDatabase.getDatabase(context)
        dao = db.transactionDao()
        allTransactions = dao.findAllTransactions()
    }

    fun loadTransactionsForAddress(address: String): LiveData<List<Transaction>> {
        return dao.findTransactionsForAddress(address = address)
    }

    fun loadAllTransactions(): LiveData<List<Transaction>> {
        return dao.findAllTransactions()
    }

    fun loadTransactions(currency: Currency): LiveData<List<Transaction>> {
        return dao.findTransactions(currency = currency.name)
    }
    fun loadTransactions_notLive(currency: Currency): Flowable<List<Transaction>> {
        return dao.findTransactions_notLive(currency = currency.name)
    }

    // TODO: rename to fetchNewTipTransactions()
    fun fetchTipTransactions(address: String, startBlock: String, endBlock: String = "latest", callback: TransactionsUpdatedWithResults) {
        tipTxDisposable?.dispose()

        tipTxDisposable = etherscanApiService.getTipTransactions(address = address, startBlock = startBlock, endBlock = endBlock)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
//                .onErrorReturn { EtherscanTxListResponse(status = "-1", message = "Error", result = listOf()) }
                .subscribe ({ response ->
                    Log.i("TX", "status = ${response.status}")
                    Log.i("TIP TX", "txlist = ${response.result}")
                    val txlist = response.result
                    if (txlist != null && !txlist.isEmpty()) {
                        var cleanedList = txlist.map { it.currency = Currency.TIP.name
                        it}
                        cleanedList = cleanedList.filter { it.value != BigInteger.ZERO }
                        dao.insertAll(cleanedList)
                    }
                    AndroidSchedulers.mainThread().scheduleDirect {
                        callback(txlist, null)
                    }

        }, {
                    Log.e("TX", "Error getting transactinos: $it")
                    Log.e("TX", "Stack: ${it.stackTrace}")
                    it.printStackTrace(System.err)
            callback(null, it)
        })
    }

    fun fetchEthTransactions(address: String, startBlock: String, endBlock: String = "latest", callback: TransactionsUpdatedWithResults) {
        ethTxDisposable?.dispose()
        ethTxDisposable = etherscanApiService.getEthTransactions(address = address, startBlock = startBlock, endBlock = endBlock)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
//                .onErrorReturn { EtherscanTxListResponse(status = "-1", message = "Error", result = listOf()) }
                .subscribe ({ response ->
                    Log.i("TX", "status = ${response.status}")
                    Log.i("ETH TX", "txlist = ${response.result}")
                    val txlist = response.result
                    if (txlist != null && !txlist.isEmpty()) {
                        var cleanedList = txlist.map { it.currency = Currency.ETH.name
                            it}
                        cleanedList = cleanedList.filter { it.value != BigInteger.ZERO }
                        dao.insertAll(cleanedList)
                    }
                    AndroidSchedulers.mainThread().scheduleDirect {
                        callback(txlist, null)
                    }

                }, {
                    Log.e("TX", "Error getting transactinos: $it")
                    Log.e("TX", "Stack: ${it.stackTrace}")
                    it.printStackTrace(System.err)
                    callback(null, it)
                })
    }

    companion object {

        val instance = TransactionRepository(App.applicationContext())

        private class insertAsyncTask: AsyncTask<Transaction, Int, Int> {

            private var mAsyncTaskDao: TransactionDao

            constructor(dao: TransactionDao) {
                mAsyncTaskDao = dao
            }

            override fun doInBackground(vararg p0: Transaction?): Int {
                var tx = p0.first()
                if (tx is Transaction) {
                    mAsyncTaskDao.insert(tx)
                    return 0
                }
                return -1
            }
        }

        private class insertManyTask: AsyncTask<Transaction, Int, Int> {

            private  var mAsyncTaskDao: TransactionDao

            constructor(dao: TransactionDao) {
                mAsyncTaskDao = dao
            }

            override fun doInBackground(vararg p0: Transaction): Int {
                val list = p0.asList()
                mAsyncTaskDao.insertAll(list)
                return 0
            }
        }
    }
}