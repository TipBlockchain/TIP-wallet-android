package io.tipblockchain.kasakasa.ui.onboarding.recovery

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository

class RecoveryPhraseViewModel: AndroidViewModel {
    var recoveryPhrase: String? = null
    var isBackedUp: Boolean = false


    constructor(application: Application): super(application) {
    }
}