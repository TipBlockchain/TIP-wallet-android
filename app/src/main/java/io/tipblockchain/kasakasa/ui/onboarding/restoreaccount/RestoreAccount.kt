package io.tipblockchain.kasakasa.ui.onboarding.restoreaccount

import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView

interface RestoreAccount {

    interface View: BaseView {
        fun onRecoveryPhraseVerified()
        fun onEmptyRecoveryPhraseError()
        fun onInvalidRecoveryPhraseLength()
        fun onInvalidRecoveryPhrase()
    }

    interface Presenter: BasePresenter<View> {
        fun checkRecoveryPhrase(phrase: String)
    }
}