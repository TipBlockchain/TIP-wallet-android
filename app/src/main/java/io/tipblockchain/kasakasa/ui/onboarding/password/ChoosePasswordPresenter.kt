package io.tipblockchain.kasakasa.ui.onboarding.password

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository
import java.lang.Error

class ChoosePasswordPresenter: ChoosePassword.Presenter {

    private var walletRepository = WalletRepository.instance
    private var existingUser: User? = null

    override fun attach(view: ChoosePassword.View) {
        super.attach(view)
        walletRepository.deleteAll()
        Log.d("ChoosePassword", "Attaching to presenter - deleting wallets")
    }

    override fun setExistingUser(user: User?) {
        existingUser = user
    }

    override fun generateWalletFromMnemonicAndPassword(mnemonic: String, password: String) {
        try {
            Schedulers.io().scheduleDirect {
                val newWallet = walletRepository.newWalletWithMnemonicAndPassword(mnemonic = mnemonic, password = password)
                AndroidSchedulers.mainThread().scheduleDirect {
                    if (newWallet != null) {
                        if (existingUser != null) {
                            if (existingUser!!.address != newWallet.wallet.address) {
                                walletRepository.delete(newWallet.wallet.address)
                                view?.onWalletNotMatchingExistingError()
                            } else {
                                view?.onWalletRestored()
                            }
                        } else {
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


    override var view: ChoosePassword.View? = null
}