package io.tipblockchain.kasakasa.data.db.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.app.App
import io.tipblockchain.kasakasa.blockchain.eth.Web3Bridge
import io.tipblockchain.kasakasa.data.db.TipRoomDatabase
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.data.db.dao.WalletDao
import io.tipblockchain.kasakasa.utils.FileUtils
import java.math.BigInteger

class WalletRepository {
    private var dao: WalletDao
    private var wallets: LiveData<List<Wallet>>
    private var primaryWallet: LiveData<Wallet?>

    private constructor(application: Application) {
        val db = TipRoomDatabase.getDatabase(application)
        dao = db.walletDao()
        wallets = dao.findAllWallets()
        primaryWallet = dao.findPrimaryWallet()
    }

    fun allWallets(): LiveData<List<Wallet>> {
        return wallets
    }

    fun primaryWallet(): LiveData<Wallet?> {
        return dao.findPrimaryWallet()
    }

    fun walletForAddress(address: String): LiveData<Wallet?> {
        return dao.findWallet( address)
    }

    fun findWalletForAddressAndCurrency(address: String, currency: Currency): LiveData<Wallet?> {
        return dao.findWalletForAddressAndCurrency(address, currency = currency.name)
    }

    fun findWalletForCurrency(currency: Currency): LiveData<Wallet?> {
        return dao.findWalletForCurrency(currency = currency.name)
    }

    fun insert(wallet: Wallet) {
        insertAsyncTask(dao).execute(wallet)
    }

    fun update(wallet: Wallet) {
        Schedulers.io().scheduleDirect{
            dao.update(wallet)
        }
    }

    fun newWalletWithPassword(password: String): NewWallet? {
        val web3Bridge = Web3Bridge()
        val bip39Wallet = web3Bridge.createBip39Wallet(password)
        val walletFile = FileUtils().fileForWalletFilename(bip39Wallet.filename)
        if (walletFile != null && walletFile.exists()) {
            val credentials = web3Bridge.loadCredentialsWithPassword(password, walletFile)
            val blockNumber = web3Bridge.latestBlock()
            val tipWallet = Wallet(address = credentials.address, filePath = walletFile.absolutePath, currency = Currency.TIP.name, blockNumber = blockNumber)
            this.insert(tipWallet)
            val ethWallet = Wallet(address = credentials.address, filePath = walletFile.absolutePath, currency = Currency.ETH.name, blockNumber = blockNumber)
            this.insert(ethWallet)
            return NewWallet(bip39Wallet.mnemonic, tipWallet)
        }

        return null
    }

    companion object {

        val instance = WalletRepository(App.application())

        private class insertAsyncTask: AsyncTask<Wallet, Int, Int> {

            private var mAsyncTaskDao: WalletDao? = null

            constructor(dao: WalletDao) {
                mAsyncTaskDao = dao
            }

            override fun doInBackground(vararg p0: Wallet?): Int {
                val wallet = p0.first()
                if (wallet is Wallet) {
                    try {
                        mAsyncTaskDao!!.insert(wallet)
                    } catch (e: Throwable) {
                        return -1
                    }
                    return 0
                }
                return -1
            }
        }
    }
}

data class NewWallet(val mnemonic: String, val wallet: Wallet)