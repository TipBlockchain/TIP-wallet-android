package io.tipblockchain.kasakasa.db.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import io.tipblockchain.kasakasa.db.TipRoomDatabase
import io.tipblockchain.kasakasa.db.entity.Wallet
import io.tipblockchain.kasakasa.db.dao.WalletDao

class WalletRepository {
    private var dao: WalletDao
    private var wallets: LiveData<List<Wallet>>
    private var primaryWallet: LiveData<Wallet>

    constructor(application: Application) {
        val db = TipRoomDatabase.getDatabase(application)
        dao = db.walletDao()
        wallets = dao.findAllWallets()
        primaryWallet = dao.findPrimaryWallet()
    }

    fun allWallets(): LiveData<List<Wallet>> {
        return wallets
    }

    fun primaryWallet(): LiveData<Wallet>? {
        return primaryWallet
    }

    companion object {
        private class insertAsyncTask: AsyncTask<Wallet, Int, Int> {

            private var mAsyncTaskDao: WalletDao? = null

            constructor(dao: WalletDao) {
                mAsyncTaskDao = dao
            }

            override fun doInBackground(vararg p0: Wallet?): Int {
                val wallet = p0.first()
                if (wallet is Wallet) {
                    mAsyncTaskDao!!.insert(wallet)
                    return 0
                }
                return -1
            }
        }
    }
}