package io.tipblockchain.kasakasa.ui.onboarding.recovery

import android.arch.lifecycle.ViewModel
import io.tipblockchain.kasakasa.blockchain.eth.Web3Bridge

class RecoveryPhraseViewModel: ViewModel() {
    var recoveryPhrase: String? = null
    var isBackedUp: Boolean = false

    fun getNewRecoveryPhrase(password: String)    {
        val web3Bridge = Web3Bridge()
        val wallet = web3Bridge.createBip39Wallet(password)

        recoveryPhrase = wallet.mnemonic
    }
}