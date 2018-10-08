package io.tipblockchain.kasakasa.data.db.repository

import android.arch.lifecycle.LiveData
import android.content.Context
import android.os.AsyncTask
import io.tipblockchain.kasakasa.data.db.TipRoomDatabase
import io.tipblockchain.kasakasa.data.db.entity.Transaction
import io.tipblockchain.kasakasa.data.db.dao.TransactionDao

class TransactionRepository {

    private var dao: TransactionDao
    private var allTransactions: LiveData<List<Transaction>>

    constructor(context: Context) {
        val db = TipRoomDatabase.getDatabase(context)
        dao = db.transactionDao()
        allTransactions = dao.findAllTransactions()
    }

    fun allTransactions(): LiveData<List<Transaction>> {
        return allTransactions
    }

    companion object {

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