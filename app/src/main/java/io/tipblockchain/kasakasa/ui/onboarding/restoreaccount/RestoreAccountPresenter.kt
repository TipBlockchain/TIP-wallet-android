package io.tipblockchain.kasakasa.ui.onboarding.restoreaccount

import io.tipblockchain.kasakasa.crypto.MnemonicUtils

class RestoreAccountPresenter: RestoreAccount.Presenter {

    override var view: RestoreAccount.View? = null

    override fun checkRecoveryPhrase(phrase: String) {
        if (phrase.isEmpty()) {
            view?.onEmptyRecoveryPhraseError()
            return
        }

        if (MnemonicUtils.isValidSeedPhrase(phrase)) {
            view?.onRecoveryPhraseVerified()
        } else {
            view?.onInvalidRecoveryPhrase()
        }
    }
}