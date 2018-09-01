package io.tipblockchain.kasakasa.ui.newaccount

import io.tipblockchain.kasakasa.blockchain.eth.Web3Bridge

class RecoveryPhraseInteractor {

    fun fetchRecoveryPhrase(password: String) {
        val wallet = Web3Bridge().createBip39Wallet(password)
    }
}