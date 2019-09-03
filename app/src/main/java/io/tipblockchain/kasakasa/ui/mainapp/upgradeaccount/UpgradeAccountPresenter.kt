package io.tipblockchain.kasakasa.ui.mainapp.upgradeaccount

import android.arch.lifecycle.Observer
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.app.PreferenceHelper
import io.tipblockchain.kasakasa.crypto.WalletUtils
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.data.db.repository.UserRepository
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository
import io.tipblockchain.kasakasa.data.responses.RecoveryPhrasePassword
import io.tipblockchain.kasakasa.utils.keystore.TipKeystore
import org.web3j.crypto.MnemonicUtils
import io.tipblockchain.kasakasa.R

class UpgradeAccountPresenter: UpgradeAccount.Presenter {

    private var walletRepo: WalletRepository = WalletRepository.instance
    private var userRepo: UserRepository = UserRepository.instance
    private var disposable: Disposable? = null
    private var logTag = javaClass.name

    override var view: UpgradeAccountActivity? = null

    override fun detach() {
        super.detach()
        disposable?.dispose()
        disposable = null
    }

    override fun recoveryPhrasAndPasswordExist(): RecoveryPhrasePassword? {
        val password = TipKeystore.readPassword()
        var recoveryPhrase = TipKeystore.readSeedPhrase()
        if (password != null && recoveryPhrase != null) {
            return RecoveryPhrasePassword(recoveryPhrase = recoveryPhrase, password = password)
        }
        return null
    }

    override fun restoreAccount(seedPhrase: String, password: String) {
        Schedulers.io().scheduleDirect {
            if (MnemonicUtils.validateMnemonic(seedPhrase)) {
                if (walletRepo.checkWalletMatchesExisting(mnemonic = seedPhrase, password = password)) {
                    this.upgradeAccount(seedPhrase, password)
                } else {
                    AndroidSchedulers.mainThread().scheduleDirect{
                        view?.onNotMatchingRecoveryPhrase()
                    }
                }
            } else {
                AndroidSchedulers.mainThread().scheduleDirect {
                    view?.onInvalidRecoveryPhrase()
                }
            }
        }
    }

    private fun upgradeAccount(seedPhrase: String, password: String) {
        val walletsObserver = walletRepo.allWallets()
        walletsObserver.observe(view!!, Observer{
            walletsObserver.removeObservers(view!!)
            Schedulers.io().scheduleDirect {
                if (it is List<Wallet>) {
                    for (wallet in it) {
                        walletRepo.makePrimary(wallet, false)
                    }
                }

                val walletFile = walletRepo.newWalletWithMnemonicAndPassword(mnemonic = seedPhrase, password = password)
                if (walletFile != null) {
                    this.updateAddress(walletFile.wallet.address)
                } else {
                    view?.onErrorUpgradingWalletError(view!!.applicationContext.getString(R.string.error_creating_upgrade_wallet))
                }
            }

        })
    }

    private fun updateAddress(address: String) {
        val address = WalletUtils.add0xIfNotExists(address)
        disposable = userRepo.updateAddress(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    AndroidSchedulers.mainThread().scheduleDirect {
                        PreferenceHelper.upgradedAccount = true
                        view?.onAccountUpgraded()
                    }

        }, {
                    Log.e(logTag, "Error updating address: ${it.localizedMessage}")
                    view?.onErrorUpgradingWalletError(it.localizedMessage)
                })
    }
}