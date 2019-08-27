package io.tipblockchain.kasakasa.ui.mainapp.upgradeaccount

import android.arch.lifecycle.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.app.PreferenceHelper
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository
import org.web3j.crypto.MnemonicUtils

class UpgradeAccountPresenter: UpgradeAccount.Presenter {

    private var repo: WalletRepository = WalletRepository.instance

    override var view: UpgradeAccountActivity? = null

    override fun restoreAccount(seedPhrase: String, password: String) {
        Schedulers.io().scheduleDirect {
            if (MnemonicUtils.validateMnemonic(seedPhrase)) {
                if (repo.checkWalletMatchesExisting(mnemonic = seedPhrase, password = password)) {
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
        val walletsObserver = repo.allWallets()
        walletsObserver.observe(view!!, Observer{
            if (it is List<Wallet>) {
                for (wallet in it) {
                    repo.makePrimary(wallet, false)
                }
            }

            val walletFile = repo.newWalletWithMnemonicAndPassword(mnemonic = seedPhrase, password = password)
            AndroidSchedulers.mainThread().scheduleDirect {
                if (walletFile == null) {
                    view?.onErrorUpgradingWalletError()
                } else {
                    PreferenceHelper.upgradedAccount = true
                    view?.onAccountUpgraded()
                }
            }
        })
    }
}