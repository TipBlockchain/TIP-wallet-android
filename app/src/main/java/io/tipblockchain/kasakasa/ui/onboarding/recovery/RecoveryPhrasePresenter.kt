package io.tipblockchain.kasakasa.ui.onboarding.recovery

import io.tipblockchain.kasakasa.crypto.WalletUtils

class RecoveryPhrasePresenter: RecoveryPhrase.Presenter {

    override var view: RecoveryPhrase.View? = null

    override fun getNewMnemonic() {
        try {
            val mnemonic = WalletUtils.generateBip39Mnemonic()
            view?.onMnemonicCreated(mnemonic)
        } catch (err: Throwable) {
            view?.onError(err)
        }
    }

}