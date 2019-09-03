package io.tipblockchain.kasakasa.data.db.repository

import android.arch.lifecycle.LiveData
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.android.example.github.AppExecutors
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
import io.tipblockchain.kasakasa.data.responses.PendingTransaction
import io.tipblockchain.kasakasa.networking.EtherscanApiService
import io.tipblockchain.kasakasa.networking.TipApiService
import org.web3j.crypto.Credentials
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.utils.Convert
import java.lang.Exception
import java.math.BigInteger
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.Future

enum class Currency {
    TIP,
    ETH
}

typealias TransactionsUpdatedWithResults = (List<Transaction>?, Throwable?) -> Unit

class TransactionRepository {

    private var dao: TransactionDao
    private var allTransactions: LiveData<List<Transaction>>
    private var tipApiService = TipApiService.instance
    private var etherscanApiService = EtherscanApiService.instance
    private var web3Bridge = Web3Bridge()
    private var executor: Executor? = null

    private var tipTxDisposable: Disposable? = null
    private var ethTxDisposable: Disposable? = null
    private var mergeTxDisposable: Disposable? = null

    private val LOG_TAG = javaClass.name

    private constructor(context: Context) {
        val db = TipRoomDatabase.getDatabase(context)
        dao = db.transactionDao()
        executor = AppExecutors().diskIO()
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

    fun postTransaction(pendingTransaction: PendingTransaction, txrReceipt: TransactionReceipt): Observable<Transaction?> {
        val tx = Transaction.from(pendingTransaction, txReceipt = txrReceipt)
        return tipApiService.addTransaction(tx)
    }

    fun insert(tx: Transaction) {
        dao.insert(tx)
    }

    fun add(tx: Transaction) {
        insertAsyncTask(dao).execute(tx)
    }

    fun sendTransaction(transaction: PendingTransaction, credentials: Credentials, gasPriceInGwei: Int, completion: ((txr: TransactionReceipt?, err: Throwable?) -> Unit)? = null) {
        try {
            executor!!.execute {
                var txReceipt: TransactionReceipt?
                var future: Future<TransactionReceipt>?
                val gasPriceInWei = Convert.toWei(gasPriceInGwei.toBigDecimal(), Convert.Unit.GWEI).toBigInteger()
                try {

                    when (transaction.currency) {
                        Currency.TIP -> future = web3Bridge.sendTipTransactionAsyncForFuture(to = transaction.to, value = transaction.value, gasPrice = gasPriceInWei, credentials = credentials)
                        Currency.ETH -> future = web3Bridge.sendEthTransactionAsyncForFuture(to = transaction.to, value = transaction.value, gasPrice = gasPriceInWei, credentials = credentials)
                    }
                    if (future == null) {
                        return@execute
                    }
                    while (!future.isDone) {
                    }
                    txReceipt = future.get()
                    postTransaction(pendingTransaction = transaction, txrReceipt = txReceipt)
                    Log.d(LOG_TAG, "Transaction receipt: $txReceipt")
                    completion?.invoke(txReceipt, null)
                } catch (e: Exception) {
                    completion?.invoke(null, e)
                }
            }

        } catch (e: Throwable) {
            completion?.invoke(null, e)
        }
    }

    // TODO: rename to fetchNewTipTransactions()
    fun fetchTipTransactions(address: String, startBlock: String, endBlock: String = "latest", callback: TransactionsUpdatedWithResults) {
        tipTxDisposable?.dispose()

        tipTxDisposable = etherscanApiService.getTipTransactions(address = address, startBlock = startBlock, endBlock = endBlock)
                .subscribeOn(Schedulers.io())
                // observe on Schedulers.io() since we write to db
                .observeOn(Schedulers.io())
                .subscribe ({ response ->
                    val txlist = response.result
                    if (txlist != null && !txlist.isEmpty()) {
                        var cleanedList = txlist.map { it.currency = Currency.TIP.name
                        it}
                        cleanedList = cleanedList.filter { it.value != BigInteger.ZERO }
                        fillTransactions(cleanedList) { mergedList, err ->
                            var resultList = mergedList
                            if (mergedList.isEmpty() && !cleanedList.isEmpty()) {
                                resultList = cleanedList
                            }
                            dao.insertAll(resultList)
                            AndroidSchedulers.mainThread().scheduleDirect {
                                callback(resultList, null)
                            }
                        }
                    }

        }, {
                    Log.e("TX", "Error getting transactions: $it")
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
                        fillTransactions(cleanedList) { mergedList, err ->
                            var resultList = mergedList
                            if (mergedList.isEmpty() && ! cleanedList.isEmpty()) {
                                resultList = cleanedList
                            }
                            dao.insertAll(resultList)
                            AndroidSchedulers.mainThread().scheduleDirect {
                                callback(resultList, null)
                            }
                        }
                    }

                }, {
                    Log.e("TX", "Error getting transactinos: $it")
                    Log.e("TX", "Stack: ${it.stackTrace}")
                    it.printStackTrace(System.err)
                    callback(null, it)
                })
    }

    private fun mergeTransactions(txlist: List<Transaction>, callback: (updatedList: List<Transaction>, error: Throwable?) -> Unit) {
        mergeTxDisposable?.dispose()
        val hashlist = txlist.map { it.hash }
        mergeTxDisposable = tipApiService.getTransactionsByHashes(txHashList = hashlist).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe ({ response ->
            if (response != null) {
                val updatedList = response.transactions
                var listToReturn: MutableList<Transaction> = mutableListOf()

                for (tx in txlist) {
                    val matchingTx = updatedList.find { it.hash == tx.hash }
                    listToReturn.add(matchingTx ?: tx)
                }
                callback(listToReturn, null)

            }
        }, {
            Log.e("TXRepo", "Error fetching tx from backend: $it")
            callback(listOf(), it)
        })
    }

    private fun fillTransactions(txlist: List<Transaction>, callback: (updatedList: List<Transaction>, error: Throwable?) -> Unit) {
        mergeTxDisposable?.dispose()
        val hashlist = txlist.map { it.hash }
        mergeTxDisposable = tipApiService.fillTransactions(txList = txlist).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe ({ response ->
            if (response != null) {
                val updatedList = response.transactions
//                var listToReturn: MutableList<Transaction> = mutableListOf()

//                for (tx in txlist) {
//                    val matchingTx = updatedList.find { it.hash == tx.hash }
//                    listToReturn.add(matchingTx ?: tx)
//                }
                callback(updatedList, null)

            }
        }, {
            Log.e("TXRepo", "Error fetching tx from backend: $it")
            callback(listOf(), it)
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