package io.tipblockchain.kasakasa.ui.onboarding.recovery

interface RecoveryPhraseView {
    fun checkTheBox(isChecked: Boolean)
    fun copyPhraseToClipboard()
    fun verifyRecoveryPhraseTapped()
}