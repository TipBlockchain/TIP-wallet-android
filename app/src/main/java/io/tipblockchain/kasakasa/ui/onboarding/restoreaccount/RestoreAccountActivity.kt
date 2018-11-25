package io.tipblockchain.kasakasa.ui.onboarding.restoreaccount

import android.content.Intent
import android.os.Bundle
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.onboarding.password.ChoosePasswordActivity
import kotlinx.android.synthetic.main.activity_restore_account.*

class RestoreAccountActivity : BaseActivity(), RestoreAccount.View {

    private var presenter: RestoreAccount.Presenter? = null
    private var recoveryPhrase: String? = null
    private var existingUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restore_account)

        existingUser = intent.getSerializableExtra(AppConstants.EXTRA_EXISTING_ACCOUNT_USER) as User?
        if (existingUser == null) {
            finish()
            return
        }
        presenter = RestoreAccountPresenter()
        presenter?.attach(this)

        nextBtn.setOnClickListener { nextButtonPressed() }
    }

    override fun onDestroy() {
        presenter?.detach()
        super.onDestroy()
    }

    override fun onRecoveryPhraseVerified() {
        navigateToChoosePassword()
    }

    override fun onEmptyRecoveryPhraseError() {
        showMessage(getString(R.string.error_empty_recovery_phrase))
    }

    override fun onInvalidRecoveryPhraseLength() {
        showMessage(getString(R.string.error_invalid_recovery_phrase_length))
    }

    override fun onInvalidRecoveryPhrase() {
        showMessage(getString(R.string.error_invalid_recovery_phrase))
    }

    private fun nextButtonPressed() {
        recoveryPhrase = seedPhraseTv.text.toString()
        presenter?.checkRecoveryPhrase(recoveryPhrase ?: "")
    }

    private fun navigateToChoosePassword() {
        val intent = Intent(this, ChoosePasswordActivity::class.java)
        intent.putExtra(AppConstants.EXTRA_RECOVERY_PHRASE, recoveryPhrase)
        intent.putExtra(AppConstants.EXTRA_EXISTING_ACCOUNT_USER, existingUser)
        startActivity(intent)
    }
}
