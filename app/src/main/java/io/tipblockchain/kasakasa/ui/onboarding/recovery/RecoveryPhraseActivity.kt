package io.tipblockchain.kasakasa.ui.onboarding.recovery

import android.arch.lifecycle.ViewModelProviders
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.databinding.ActivityRecoveryPhraseBinding
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.onboarding.verifyrecovery.VerifyRecoveryPhraseActivity

import kotlinx.android.synthetic.main.activity_recovery_phrase.*

class RecoveryPhraseActivity : BaseActivity(), RecoveryPhrase.View {

    private var viewModel: RecoveryPhraseViewModel? = null
    private var presenter: RecoveryPhrase.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityRecoveryPhraseBinding = DataBindingUtil.setContentView(this, R.layout.activity_recovery_phrase)
        viewModel = getViewModel()
        binding.viewModel = viewModel

        verifyBtn.setOnClickListener {
            this.verifyRecoveryPhraseTapped()
        }

        copyBtn.setOnClickListener {
            this.copyPhraseToClipboard()
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            this.checkTheBox(isChecked)
        }

        presenter = RecoveryPhrasePresenter()
        presenter?.attach(this)
        presenter?.getNewMnemonic()
    }

    override fun onDestroy() {
        presenter?.detach()
        super.onDestroy()
    }

    override fun onError(error: Throwable) {
        showMessage(getString(R.string.generic_error_with_param, error.localizedMessage))
    }

    override fun onMnemonicCreated(mnemonic: String) {
        viewModel?.recoveryPhrase = mnemonic
    }

    fun copyPhraseToClipboard() {
        val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData: ClipData = ClipData.newPlainText("recovery phrase", viewModel?.recoveryPhrase)
        clipboard.primaryClip = clipData

        this.showMessage(getString(R.string.recovery_phrase_copied))
    }

    fun checkTheBox(isChecked: Boolean) {
        viewModel?.isBackedUp = isChecked
    }

    fun verifyRecoveryPhraseTapped() {
        if (viewModel?.isBackedUp == true) {
            this.navigateToVerifyRecovery()
        } else {
            showNotBackedUpError()
        }
    }

    private fun showNotBackedUpError() {
        showMessage(getString(R.string.recovery_phrase_must_be_copied))
    }

    private fun navigateToVerifyRecovery() {
        val intent = Intent(this, VerifyRecoveryPhraseActivity::class.java)
        intent.putExtra(AppConstants.EXTRA_RECOVERY_PHRASE, viewModel?.recoveryPhrase)
        startActivity(intent)
    }

    private fun getViewModel() = ViewModelProviders.of(this).get(RecoveryPhraseViewModel::class.java)
}

