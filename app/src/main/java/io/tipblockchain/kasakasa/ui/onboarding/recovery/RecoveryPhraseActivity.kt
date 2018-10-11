package io.tipblockchain.kasakasa.ui.onboarding.recovery

import android.arch.lifecycle.ViewModelProviders
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.databinding.ActivityRecoveryPhraseBinding
import io.tipblockchain.kasakasa.ui.onboarding.profile.OnboardingUserProfileActivity

import kotlinx.android.synthetic.main.activity_recovery_phrase.*

class RecoveryPhraseActivity : AppCompatActivity(), RecoveryPhraseView {

    private var password :String = ""
    private lateinit var viewModel: RecoveryPhraseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityRecoveryPhraseBinding = DataBindingUtil.setContentView(this, R.layout.activity_recovery_phrase)
        password = intent.getStringExtra("password")
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

        viewModel.createNewWallet(password)
    }

    override fun copyPhraseToClipboard() {
        val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData: ClipData = ClipData.newPlainText("recovery phrase", viewModel.recoveryPhrase)
        clipboard.primaryClip = clipData

        this.showMessage(getString(R.string.recovery_phrase_copied))
    }

    override fun checkTheBox(isChecked: Boolean) {
        Log.i("GIT", "isChecked = $isChecked")
        viewModel.isBackedUp = isChecked
    }

    override fun verifyRecoveryPhraseTapped() {
        if (viewModel.isBackedUp) {
            this.navigateToVerifyRecovery()
        } else {
            showNotBackedUpError()
        }
    }

    private fun showNotBackedUpError() {
        showMessage(getString(R.string.recovery_phrase_must_be_copied))
    }

    private fun showMessage(message: String) {
        var fromView: View = findViewById(R.id.constraintLayout)
        Snackbar.make(fromView, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }

    private fun navigateToVerifyRecovery() {
        startActivity(Intent(this, OnboardingUserProfileActivity::class.java))
    }

    private fun getViewModel() = ViewModelProviders.of(this).get(RecoveryPhraseViewModel::class.java)
}

