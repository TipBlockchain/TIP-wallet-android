package io.tipblockchain.kasakasa.ui.mainapp.upgradeaccount

import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.app.PreferenceHelper
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
                    view?.onNotMatchingRecoveryPhrase()
                }
            } else {
                view?.onInvalidRecoveryPhrase()
            }
        }
    }

    private fun upgradeAccount(seedPhrase: String, password: String) {
        val walletFile = repo.newWalletWithMnemonicAndPassword(mnemonic = seedPhrase, password = password)
        if (walletFile == null) {
            view?.onErrorUpgradingWalletError()
        } else {
            PreferenceHelper.upgradedAccount = true
            view?.onAccountUpgraded()
        }
    }
}