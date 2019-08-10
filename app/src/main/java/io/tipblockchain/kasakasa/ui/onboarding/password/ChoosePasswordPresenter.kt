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
import java.lang.Error

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

    override fun generateWalletFromMnemonicAndPassword(mnemonic: String, password: String) {
        try {
            Schedulers.io().scheduleDirect {
                walletRepository.deleteAllDirect()
                val newWallet = walletRepository.newWalletWithMnemonicAndPassword(mnemonic = mnemonic, password = password, useBip44 = false)
                AndroidSchedulers.mainThread().scheduleDirect {
                    if (newWallet != null) {
                        if (existingUser != null) {
                            if (existingUser!!.address != newWallet.wallet.address) {
                                walletRepository.delete(newWallet.wallet.address)
                                view?.onWalletNotMatchingExistingError()
                            } else {
                                this.saveToKeystore(password = password, seedPhrase = mnemonic)
                                view?.onWalletRestored()
                            }
                        } else {
                            this.saveToKeystore(password = password, seedPhrase = mnemonic)
                            PreferenceHelper.upgradedAccount = true
                            view?.onWalletCreated()
                        }
                    } else {
                        view?.onWalletCreationError(Error())
                    }
                }
            }
        } catch (err: Throwable) {
            AndroidSchedulers.mainThread().scheduleDirect {
                view?.onWalletCreationError(err)
            }
        }
    }

    override fun checkAndGenerateWallet(mnemonic: String, password: String) {

        Schedulers.io().scheduleDirect{
            try {
                walletRepository.deleteAllDirect()

                var generateTwoWallets = false
                var walletFile: WalletFile
                var finalWalletFile: WalletFile? = null

                if (existingUser != null) {
                    walletFile = walletRepository.bip44WalletFileFromMnemonic(mnemonic, password)
                    Log.d(LOG_TAG, "walletFile.address= ${walletFile.address}, user.address = ${existingUser!!.address}")
                    if (WalletUtils.add0xIfNotExists(walletFile.address) == existingUser!!.address) {
                        finalWalletFile = walletFile
                    } else {
                        walletFile = walletRepository.bip39WalletFileFromMnemonic(mnemonic, password)
                        if (WalletUtils.add0xIfNotExists(walletFile.address) == existingUser!!.address) {
                            finalWalletFile = walletFile
                            generateTwoWallets = true
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
                    val isPrimary = !generateTwoWallets
                    walletRepository.saveWalletFile(finalWalletFile, isPrimary)
                    if (generateTwoWallets) {
                        val secondWalletFile = walletRepository.bip44WalletFileFromMnemonic(mnemonic, password)
                        walletRepository.saveWalletFile(secondWalletFile, isPrimary = true)
                    }
                    PreferenceHelper.upgradedAccount = true
                    this.saveToKeystore(password = password, seedPhrase = mnemonic)
                    AndroidSchedulers.mainThread().scheduleDirect {
                        if (existingUser == null) {
                            view?.onWalletCreated()
                        } else {
                            view?.onWalletRestored()
                        }
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