package io.tipblockchain.kasakasa.ui.onboarding.verifyrecovery

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.newaccount.ChooseUsernameActivity
import io.tipblockchain.kasakasa.ui.onboarding.password.ChoosePasswordActivity
import io.tipblockchain.kasakasa.ui.onboarding.profile.OnboardingUserProfileActivity

import kotlinx.android.synthetic.main.activity_verify_recovery_phrase.*

class VerifyRecoveryPhraseActivity : BaseActivity(), VerifyRecoveryPhrase.View {

    private var presenter: VerifyRecoveryPhrase.Presenter? = null
    private var recoveryPhrase: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_recovery_phrase)

        val recoveryPhrase = intent.getStringExtra(AppConstants.EXTRA_RECOVERY_PHRASE)

        verifyBtn.setOnClickListener { verifyRecoveryPhrase() }
        if (recoveryPhrase == null || recoveryPhrase.isEmpty()) {
            finish()
        }

        setupPresenter(recoveryPhrase!!)
    }

    override fun onDestroy() {
        presenter?.detach()
        super.onDestroy()
    }
    override fun onWordsRemoved(phrase: String, firstIndex: Int, secondIndex: Int) {
        recoveryPhrase = phrase
        seedPhraseTv.text = phrase
    }

    override fun onPhraseVerified() {
       showMessage(getString(R.string.recovery_phrase_verified))
        Handler().postDelayed ({
            navigateToChoosePasswordSCreen()
        }, 2000)
    }

    override fun onVerificationError() {
        verifyBtn.isEnabled = true
        showMessage(getString(R.string.error_verifying_recovery_phrase))
    }

    private fun setupPresenter(phrase: String) {
        presenter = VerifyRecoveryPhrasePresenter()
        presenter?.attach(this)
        presenter?.setRecoveryPhrase(phrase)
        presenter?.removeWords()
    }

    private fun validateForm(): Boolean {
        missingWord1Tv.error = null
        missingWord2Tv.error = null

        if (missingWord1Tv.text == null || missingWord1Tv.text!!.isEmpty()) {
            missingWord1Tv.error = getString(R.string.error_empty_field)
            return false
        }

        if (missingWord2Tv.text == null || missingWord2Tv.text!!.isEmpty()) {
            missingWord2Tv.error = getString(R.string.error_empty_field)
            return false
        }

        return true
    }

    private fun verifyRecoveryPhrase() {
        if (recoveryPhrase.isEmpty()) {
            showMessage(getString(R.string.error_invalid_recovery_phrase))
        }

        if (validateForm()) {
            verifyBtn.isEnabled = false
            val word1 = missingWord1Tv.text.toString()
            val word2 = missingWord2Tv.text.toString()
            presenter?.verifyRecoveryPhrase(recoveryPhrase, word1, word2)
        }
    }

    private fun navigateToChoosePasswordSCreen() {
       val intent = Intent(this, ChoosePasswordActivity::class.java)
        intent.putExtra(AppConstants.EXTRA_RECOVERY_PHRASE, recoveryPhrase)
        startActivity(intent)
    }

}
