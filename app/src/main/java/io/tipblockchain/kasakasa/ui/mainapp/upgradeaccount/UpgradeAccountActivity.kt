package io.tipblockchain.kasakasa.ui.mainapp.upgradeaccount

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.widget.EditText
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.BaseView

import kotlinx.android.synthetic.main.activity_upgrade_account.*
import kotlinx.android.synthetic.main.content_upgrade_account.*
import org.web3j.crypto.MnemonicUtils

class UpgradeAccountActivity : BaseActivity(), BaseView {

    private var presenter: UpgradeAccountPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.title = getString(R.string.upgrade_account)
        setContentView(R.layout.activity_upgrade_account)
        setSupportActionBar(toolbar)
        setupPresenter()
        checkForRecoveryAndPassword()
        upgradeBtn.setOnClickListener {
            this.upgradeAccount()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detach()
        presenter = null
    }

    private fun setupPresenter() {
        presenter = UpgradeAccountPresenter()
        presenter?.attach(this)
    }

    private fun checkForRecoveryAndPassword() {
        val recoveryPassword = presenter?.recoveryPhrasAndPasswordExist()
        if (recoveryPassword != null) {
            disableInteraction()

            this.showEnterPasswordDialog(onCompletion = { password ->
                if (password == recoveryPassword.password) {
                    presenter?.restoreAccount(seedPhrase = recoveryPassword.recoveryPhrase, password = recoveryPassword.password)
                } else {
                    this.showOkDialog(getString(R.string.dialog_title_invalid_password), getString(R.string.dialog_message_invalid_password))
                }
            })
        }
    }

    private fun disableInteraction() {
        upgradeBtn.isEnabled = false
        seedPhraseTv.isEnabled = false
    }

    private fun upgradeAccount() {
        val seedPhrase = seedPhraseTv.text.toString().trim()
        if (seedPhrase.isEmpty()) {
            this.showMessage(getString(R.string.enter_seed_phrase))
            return
        }
        if (!MnemonicUtils.validateMnemonic(seedPhrase)) {
            this.showOkDialog(title = getString(R.string.error_invalid_recovery_phrase), message = getString(R.string.error_invalid_recovery_phrase_message))
            return
        }
        this.showEnterPasswordDialog(onCompletion = { password ->
            presenter?.restoreAccount(seedPhrase = seedPhrase, password = password)
        }, onCancel = null)
    }

    override fun showEnterPasswordDialog(onCompletion: (String) -> Unit, onCancel: (() -> Unit)?) {
        val view = layoutInflater.inflate(R.layout.dialog_enter_password_upgrade, null)
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle(getString(R.string.enter_password))
        alertDialog.setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_secure))
        alertDialog.setCancelable(false)

        val passwordView =  view.findViewById(R.id.passwordTv) as EditText

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.okay)) { _, _ ->
            val password = passwordView.text.toString()
            onCompletion(password)
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
            if (onCancel != null) {
                onCancel()
            }
        }

        alertDialog.setView(view)
        alertDialog.show()
    }

    fun onAccountUpgraded() {
        this.showOkDialog(title = getString(R.string.wallets_upgraded), message = getString(R.string.wallets_upgraded_message), onClickListener = DialogInterface.OnClickListener { dialog, which ->
            this.finish()
        })
    }


    fun onInvalidRecoveryPhrase() {
        this.showMessage(getString(R.string.error_invalid_recovery_phrase))
    }

    fun onNotMatchingRecoveryPhrase() {
        this.showMessage(getString(R.string.error_verifying_recovery_phrase))
    }

    fun onErrorUpgradingWalletError(message: String) {
        this.showOkDialog(getString(R.string.error_updatiing_user_info), message)
    }

}
