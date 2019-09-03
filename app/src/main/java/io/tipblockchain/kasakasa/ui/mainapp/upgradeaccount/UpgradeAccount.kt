package io.tipblockchain.kasakasa.ui.mainapp.upgradeaccount

import io.tipblockchain.kasakasa.data.responses.RecoveryPhrasePassword
import io.tipblockchain.kasakasa.ui.BasePresenter

interface UpgradeAccount {
    interface Presenter: BasePresenter<UpgradeAccountActivity> {
        fun recoveryPhrasAndPasswordExist(): RecoveryPhrasePassword?
        fun restoreAccount(seedPhrase: String, password: String)
    }
}