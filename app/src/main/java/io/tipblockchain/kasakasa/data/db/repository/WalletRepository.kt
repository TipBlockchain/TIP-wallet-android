package io.tipblockchain.kasakasa.data.db.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import android.util.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.app.App
import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.blockchain.eth.Web3Bridge
import io.tipblockchain.kasakasa.config.AppProperties
import io.tipblockchain.kasakasa.crypto.WalletUtils
import io.tipblockchain.kasakasa.data.db.TipRoomDatabase
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.data.db.dao.WalletDao
import io.tipblockchain.kasakasa.utils.FileUtils
import org.web3j.crypto.Bip39Wallet
import org.web3j.crypto.WalletFile
import java.io.File

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

    fun findWalletForAddressAndCurrency(address: String, currency: Currency): Observable<Wallet?> {
        return dao.findWalletForAddressAndCurrency(address, currency = currency.name).toObservable()
    }

    fun findWalletForAddressAndCurrencyAsLiveData(address: String, currency: Currency): LiveData<Wallet?> {
        return dao.findWalletForAddressAndCurrencyLiveData(address, currency = currency.name)
    }

    fun findWalletForCurrency(currency: Currency): LiveData<Wallet?> {
        return dao.findWalletForCurrency(currency = currency.name)
    }

    fun insert(wallet: Wallet) {
        insertAsyncTask(dao).execute(wallet)
    }

    fun makePrimary(wallet: Wallet, primary: Boolean) {
        wallet.isPrimary = primary
        this.updateDirect(wallet)
    }

    fun update(wallet: Wallet) {
        Schedulers.io().scheduleDirect{
            dao.update(wallet)
        }
    }

    fun updateDirect(wallet: Wallet) {
        dao.update(wallet)
    }

    fun delete(address: String) {
        Schedulers.io().scheduleDirect {
            dao.delete(address)
        }
    }

    fun delete(wallet: Wallet) {
        Schedulers.io().scheduleDirect {
            dao.delete(wallet)
        }
    }

    fun deleteAll() {
        Schedulers.io().scheduleDirect {
            dao.deleteAll()
        }
    }

    fun deleteAllDirect() {
       dao.deleteAll()
    }

    fun bip39WalletFileFromMnemonic(mnemonic: String, password: String): WalletFile {
        return WalletUtils.getBip39WalletFileFromMnemonic(mnemonic, password, destinationDirectory = FileUtils().walletsDir())
    }

    fun bip44WalletFileFromMnemonic(mnemonic: String, password: String): WalletFile {
        return WalletUtils.getBip44WalletFileFromMnemonic(mnemonic, password, FileUtils().walletsDir())
    }
    fun checkWalletMatchesExisting(mnemonic: String, password: String): Boolean {
        var walletFile = WalletUtils.getBip39WalletFileFromMnemonic(mnemonic, password, destinationDirectory = FileUtils().walletsDir())
        val walletList = dao.getAllWallets()
        var walletMatch = false
        Log.i(LOG_TAG, "Wallets are: $walletList")
        if (walletList != null) {
            for (wallet in walletList) {
                Log.i(LOG_TAG, "wallet.address = ${wallet.address} <=> walletFile.address = ${walletFile.address}")
                if (wallet.address == WalletUtils.add0xIfNotExists(walletFile.address)) {
                    walletMatch = true
                    break
                }
            }
        }
        return walletMatch
    }

    fun newWalletWithMnemonicAndPassword(mnemonic: String, password: String, useBip44: Boolean = true): NewWallet? {
        val web3Bridge = Web3Bridge()
        var bip39Wallet: Bip39Wallet? = null
        when (useBip44) {
            true -> bip39Wallet = WalletUtils.generateBip44WalletFromMnemonic(mnemonic = mnemonic, password = password, destinationDirectory = FileUtils().walletsDir())
            false -> bip39Wallet = WalletUtils.generateBip39WalletFromMnemonic(mnemonic = mnemonic, password = password, destinationDirectory = FileUtils().walletsDir())
        }
        val walletFile = FileUtils().fileForWalletFilename(bip39Wallet!!.filename)
        if (walletFile != null && walletFile.exists()) {
            val credentials = web3Bridge.loadCredentialsWithPassword(password, walletFile)
            val blockNumber = AppProperties.get(AppConstants.APP_START_BLOCK).toBigInteger()
            val tipWallet = Wallet(address = WalletUtils.add0xIfNotExists(credentials.address), filePath = walletFile.absolutePath, currency = Currency.TIP.name, blockNumber = blockNumber, startBlockNumber = blockNumber)
            this.insert(tipWallet)
            val ethWallet = Wallet(address = WalletUtils.add0xIfNotExists(credentials.address), filePath = walletFile.absolutePath, currency = Currency.ETH.name, blockNumber = blockNumber, startBlockNumber = blockNumber)
            this.insert(ethWallet)
            return NewWallet(bip39Wallet!!.mnemonic, tipWallet)
        }

        return null
    }

    fun saveWalletFile(walletFile: WalletFile, isPrimary: Boolean = false): File? {
        val file = WalletUtils.saveWalletFile(walletFile, destinationDirectory = FileUtils().walletsDir())

        if (file != null && file.exists()) {
            val blockNumber = AppProperties.get(AppConstants.APP_START_BLOCK).toBigInteger()
            val tipWallet = Wallet(address = WalletUtils.add0xIfNotExists(walletFile.address), filePath = file.absolutePath, currency = Currency.TIP.name, blockNumber = blockNumber, startBlockNumber = blockNumber, isPrimary = isPrimary)
            this.insert(tipWallet)
            val ethWallet = Wallet(address = WalletUtils.add0xIfNotExists(walletFile.address), filePath = file.absolutePath, currency = Currency.ETH.name, blockNumber = blockNumber, startBlockNumber = blockNumber, isPrimary = isPrimary)
            this.insert(ethWallet)
        }
        return file
    }

    companion object {

        const val LOG_TAG = "WalletRepository"

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
