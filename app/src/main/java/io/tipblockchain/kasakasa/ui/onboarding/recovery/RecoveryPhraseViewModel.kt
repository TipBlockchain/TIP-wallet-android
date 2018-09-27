package io.tipblockchain.kasakasa.ui.onboarding.recovery

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import io.tipblockchain.kasakasa.db.repository.WalletRepository

class RecoveryPhraseViewModel: AndroidViewModel {
    var recoveryPhrase: String? = null
    var isBackedUp: Boolean = false

    private var walletRepository: WalletRepository

    constructor(application: Application): super(application) {
        walletRepository = WalletRepository(application)
    }

    fun createNewWallet(password: String)    {
        val newWallet = walletRepository.newWalletWithPassword(password)

        recoveryPhrase = newWallet?.mnemonic
    }
}