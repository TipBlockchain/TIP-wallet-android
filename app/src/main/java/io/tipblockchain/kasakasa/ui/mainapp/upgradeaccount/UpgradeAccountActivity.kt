package io.tipblockchain.kasakasa.ui.mainapp.upgradeaccount

import android.content.DialogInterface
import android.os.Bundle
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
        })
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

    fun onErrorUpgradingWalletError() {

    }

}
