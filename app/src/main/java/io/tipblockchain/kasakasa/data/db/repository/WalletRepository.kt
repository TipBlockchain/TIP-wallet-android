package io.tipblockchain.kasakasa.db.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import io.tipblockchain.kasakasa.blockchain.eth.Web3Bridge
import io.tipblockchain.kasakasa.db.TipRoomDatabase
import io.tipblockchain.kasakasa.db.entity.Wallet
import io.tipblockchain.kasakasa.db.dao.WalletDao
import io.tipblockchain.kasakasa.utils.FileUtils

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

    fun insert(wallet: Wallet) {
        insertAsyncTask(dao).execute(wallet)
    }

    fun newWalletWithPassword(password: String): NewWallet? {
        val web3Bridge = Web3Bridge()
        val bip39Wallet = web3Bridge.createBip39Wallet(password)
        val walletFile = FileUtils().fileForWalletFilename(bip39Wallet.filename)
        if (walletFile != null && walletFile.exists()) {
            val credentials = web3Bridge.loadCredentialsWithPassword(password, walletFile)
            val wallet = Wallet(credentials.address, walletFile.absolutePath)
            this.insert(wallet)
            return NewWallet(bip39Wallet.mnemonic, wallet)
        }

        return null
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

data class NewWallet(val mnemonic: String, val wallet: Wallet){}