package io.tipblockchain.kasakasa.ui.backup

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView

import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.EditText
import io.reactivex.android.schedulers.AndroidSchedulers

import kotlinx.android.synthetic.main.activity_backup_account.*
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.utils.keystore.TipKeystore

/**
 * A login screen that offers login via email/password.
 */
class BackupAccountActivity : BaseActivity() {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        LOG_TAG = javaClass.name
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup_account)

        copyBtn.setOnClickListener {
            this.copyPhraseToClipboard()
        }

        backupBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, seedPhraseTv.text)
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, getString(R.string.backup_using)), null)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onStart() {
        super.onStart()
        this.enableButtons(false)
        this.showEnterPasswordDialog()
    }

    private fun showEnterPasswordDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_enter_password, null)
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle(getString(R.string.enter_password))
        alertDialog.setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_secure))
        alertDialog.setCancelable(false)

        val passwordView =  view.findViewById(R.id.passwordTv) as EditText

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.okay)) { _, _ ->
            val password = passwordView.text.toString()
            if (checkPassword(password)) {
                this.enableButtons(true)
                showRecoveryPhrase()
            } else {
                showInvalidPasswordDialog()
            }
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
            finish()
        }

        alertDialog.setView(view)
        alertDialog.show()
    }


    private fun showInvalidPasswordDialog() {
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle(getString(R.string.dialog_title_invalid_password))
        alertDialog.setMessage(getString(R.string.dialog_message_invalid_password))
        alertDialog.setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_secure))
        alertDialog.setCancelable(false)

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.try_again)) { _, _ ->
            this.showEnterPasswordDialog()
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
            this.finish()
        }

        alertDialog.show()
    }

    private fun copyPhraseToClipboard() {
        val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData: ClipData = ClipData.newPlainText("recovery phrase", seedPhraseTv.text)
        clipboard.primaryClip = clipData

        this.showMessage(getString(R.string.recovery_phrase_copied))
    }

    private fun enableButtons(enable: Boolean) {
        copyBtn.isEnabled = enable
        backupBtn.isEnabled = enable
    }

    private fun checkPassword(password: String): Boolean {
        val storedPassword = TipKeystore.readPassword()
        Log.w(LOG_TAG, "password = $password, stored=$storedPassword")
        if (storedPassword != null) {
            return storedPassword == password
        }
        return false
    }

    private fun showRecoveryPhrase() {
        val recoveryPhrase = TipKeystore.readSeedPhrase()
        if (recoveryPhrase != null) {
            seedPhraseTv.text = recoveryPhrase
        }
    }

}
