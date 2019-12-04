package io.tipblockchain.kasakasa.ui.onboarding.password

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.app.PreferenceHelper
import io.tipblockchain.kasakasa.crypto.WalletUtils
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository
import io.tipblockchain.kasakasa.utils.keystore.TipKeystore
import org.web3j.crypto.WalletFile
import kotlin.Error

class ChoosePasswordPresenter: ChoosePassword.Presenter {

    private var walletRepository = WalletRepository.instance
    private var existingUser: User? = null
    private val LOG_TAG = javaClass.name

    override fun attach(view: ChoosePassword.View) {
        super.attach(view)
        walletRepository.deleteAll()
        Log.d(LOG_TAG, "Attaching to presenter - deleting wallets")
    }

    override fun setExistingUser(user: User?) {
        existingUser = user
    }

//    override fun generateWalletFromMnemonicAndPassword(mnemonic: String, password: String) {
//        try {
//            Schedulers.io().scheduleDirect {
//                walletRepository.deleteAllDirect()
//                val newWallet = walletRepository.newWalletWithMnemonicAndPassword(mnemonic = mnemonic, password = password, useBip44 = false)
//                AndroidSchedulers.mainThread().scheduleDirect {
//                    if (newWallet != null) {
//                        if (existingUser != null) {
//                            if (existingUser!!.address != newWallet.wallet.address) {
//                                walletRepository.delete(newWallet.wallet.address)
//                                view?.onWalletNotMatchingExistingError()
//                            } else {
//                                this.saveToKeystore(password = password, seedPhrase = mnemonic)
//                                view?.onWalletRestored()
//                            }
//                        } else {
//                            this.saveToKeystore(password = password, seedPhrase = mnemonic)
//                            PreferenceHelper.upgradedAccount = true
//                            view?.onWalletCreated()
//                        }
//                    } else {
//                        view?.onWalletCreationError(Error())
//                    }
//                }
//            }
//        } catch (err: Throwable) {
//            AndroidSchedulers.mainThread().scheduleDirect {
//                view?.onWalletCreationError(err)
//            }
//        }
//    }

    override fun checkAndGenerateWallet(mnemonic: String, password: String) {

        Schedulers.io().scheduleDirect{
            try {
                walletRepository.deleteAllDirect()

                var legacyWalletCreated = false
                var walletFile: WalletFile
                var finalWalletFile: WalletFile? = null
                var legacyWalletFile: WalletFile? = null
                var createTwoWallets = false

                if (existingUser != null) {
                    if (existingUser!!.isLegacy == true) {
                        legacyWalletFile = walletRepository.bip39WalletFileFromMnemonic(mnemonic, password)
                        if (WalletUtils.add0xIfNotExists(legacyWalletFile.address.toLowerCase()) == existingUser!!.address.toLowerCase()) {
                            finalWalletFile = legacyWalletFile
                            legacyWalletCreated = true
                        } else {
                            walletFile = walletRepository.bip44WalletFileFromMnemonic(mnemonic, password)
                            if (WalletUtils.add0xIfNotExists(walletFile.address.toLowerCase()) == existingUser!!.address.toLowerCase()) {
                                finalWalletFile = walletFile
                                createTwoWallets = true
                            } else {
                                AndroidSchedulers.mainThread().scheduleDirect{
                                    view?.onWalletNotMatchingExistingError()
                                }
                                return@scheduleDirect
                            }
                        }
                    } else {
                        walletFile = walletRepository.bip44WalletFileFromMnemonic(mnemonic, password)
                        Log.d(LOG_TAG, "walletFile.address= ${walletFile.address}, user.address = ${existingUser!!.address}")
                        if (WalletUtils.add0xIfNotExists(walletFile.address.toLowerCase()) == existingUser!!.address.toLowerCase()) {
                            finalWalletFile = walletFile
                        } else {
                            AndroidSchedulers.mainThread().scheduleDirect{
                                view?.onWalletNotMatchingExistingError()
                            }
                            return@scheduleDirect
                        }
                    }
                } else {
                    walletFile = walletRepository.bip44WalletFileFromMnemonic(mnemonic, password)
                    finalWalletFile = walletFile
                }
                if (finalWalletFile != null) {
                    var walletSuffix = if (legacyWalletCreated) "1" else null
                    val isPrimary = !legacyWalletCreated
                    walletRepository.saveWalletFile(finalWalletFile, isPrimary, walletSuffix)
                    if (!legacyWalletCreated) {
                        PreferenceHelper.upgradedAccount = true
//                        val secondWalletFile = walletRepository.bip44WalletFileFromMnemonic(mnemonic, password)
//                        walletSuffix = if (legacyWalletCreated) "2" else null
//                        walletRepository.saveWalletFile(secondWalletFile, isPrimary = true, walletSuffix = walletSuffix)
                    }
                    if (createTwoWallets && legacyWalletFile != null) {
                        walletRepository.saveWalletFile(legacyWalletFile, isPrimary = false, walletSuffix = "2")
                    }

                    this.saveToKeystore(password = password, seedPhrase = mnemonic)
                    AndroidSchedulers.mainThread().scheduleDirect {
                        if (existingUser == null) {
                            view?.onWalletCreated()
                        } else {
                            view?.onWalletRestored()
                        }
                    }
                } else {
                    AndroidSchedulers.mainThread().run {
                        view?.onWalletCreationError(Error("Error creating wallet"))
                    }
                }
            } catch (err: Throwable) {
                AndroidSchedulers.mainThread().scheduleDirect {
                    view?.onWalletCreationError(err)
                }
            }
        }
    }

    private fun saveToKeystore(password: String, seedPhrase: String) {
        TipKeystore.savePassword(password)
        TipKeystore.saveSeedPhrase(seedPhrase)
    }


    override var view: ChoosePassword.View? = null
}