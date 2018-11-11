package io.tipblockchain.kasakasa.data.db.repository

import android.arch.lifecycle.LiveData
import android.content.Context
import android.os.AsyncTask
import android.util.Log
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
    private var web3Bridge = Web3Bridge()
    private var etherscanApiService = EtherscanApiService.instance

    private var tipTxDisposable: Disposable? = null
    private var ethTxDisposable: Disposable? = null

    private constructor(context: Context) {
        val db = TipRoomDatabase.getDatabase(context)
        dao = db.transactionDao()
        allTransactions = dao.findAllTransactions()
    }

    fun allTransactions(): LiveData<List<Transaction>> {
        return allTransactions
    }

    fun sendTransaction(amount: BigInteger, currency: Currency) {

    }

    fun fetchTipTransactions(address: String, startBlock: String, callback: TransactionsUpdatedWithResults) {
        tipTxDisposable?.dispose()

        tipTxDisposable = etherscanApiService.getTipTransactions(address = address, startBlock = startBlock, endBlock = "latest")
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
//                .onErrorReturn { EtherscanTxListResponse(status = "-1", message = "Error", result = listOf()) }
                .subscribe ({ response ->
                    Log.i("TX", "status = ${response.status}")
                    Log.i("TIP TX", "txlist = ${response.result}")
                    val txlist = response.result
                    if (txlist != null && !txlist.isEmpty()) {
                        val addedCurrency: List<Transaction> = txlist.map { it.currency = Currency.TIP.name
                        it}
                        dao.insertAll(addedCurrency)
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

    fun fetchEthTransactions(address: String, startBlock: String, callback: TransactionsUpdatedWithResults) {
        ethTxDisposable?.dispose()
        ethTxDisposable = etherscanApiService.getEthTransactions(address = address, startBlock = startBlock, endBlock = "latest")
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
//                .onErrorReturn { EtherscanTxListResponse(status = "-1", message = "Error", result = listOf()) }
                .subscribe ({ response ->
                    Log.i("TX", "status = ${response.status}")
                    Log.i("ETH TX", "txlist = ${response.result}")
                    val txlist = response.result
                    if (txlist != null && !txlist.isEmpty()) {
                        val addedCurrency: List<Transaction> = txlist.map { it.currency = Currency.ETH.name
                            it}
                        dao.insertAll(addedCurrency)
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