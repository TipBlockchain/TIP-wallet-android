package io.tipblockchain.kasakasa.ui.mainapp.upgradeaccount

import io.tipblockchain.kasakasa.ui.BasePresenter

interface UpgradeAccount {
    interface Presenter: BasePresenter<UpgradeAccountActivity> {
        fun restoreAccount(seedPhrase: String, password: String)
    }
}