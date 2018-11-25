package io.tipblockchain.kasakasa.ui.onboarding.recovery

import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView

interface RecoveryPhrase {
    interface View: BaseView {
        fun onError(error: Throwable)
        fun onMnemonicCreated(mnemonic: String)
    }

    interface Presenter: BasePresenter<View> {
        fun getNewMnemonic()
    }
}