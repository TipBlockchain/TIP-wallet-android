package io.tipblockchain.kasakasa.ui.onboarding.verifyrecovery

import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView

interface VerifyRecoveryPhrase {

    interface View: BaseView {
        fun onWordsRemoved(phrase: String, firstIndex: Int, secondIndex: Int)
        fun onPhraseVerified()
        fun onVerificationError()
    }

    interface Presenter: BasePresenter<View> {
        fun setRecoveryPhrase(phrase: String)
        fun removeWords()
        fun verifyRecoveryPhrase(phrase: String, word1: String, word2: String)
    }
}