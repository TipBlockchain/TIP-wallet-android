package io.tipblockchain.kasakasa.ui.onboarding.demoaccount

import android.content.Intent
import android.os.Bundle
import com.squareup.picasso.Picasso
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.onboarding.recovery.RecoveryPhraseActivity
import io.tipblockchain.kasakasa.ui.onboarding.restoreaccount.RestoreAccountActivity
import kotlinx.android.synthetic.main.content_demo_account.*

class DemoAccountActivity : BaseActivity() {

    private var demoAccountUser: User? = null
    private var existingAccountUser: User? = null
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo_account)

        demoAccountUser = intent.getSerializableExtra(AppConstants.EXTRA_DEMO_ACCOUNT_USER) as User?
        existingAccountUser = intent.getSerializableExtra(AppConstants.EXTRA_EXISTING_ACCOUNT_USER) as User?
        if (demoAccountUser == null && existingAccountUser == null) {
            finish()
            return
        }
        currentUser = demoAccountUser ?: existingAccountUser
        updateUI()
        createWalletBtn.setOnClickListener { navigateToNextScreen() }
    }

    private fun updateUI() {
        if (currentUser == null) {
            return
        }

        usernameTv.text = currentUser!!.username
        if (currentUser?.originalPhotoUrl != null) {
            Picasso.get().load(currentUser!!.originalPhotoUrl).into(displayImageView)
        } else {
            Picasso.get().load(R.drawable.avatar_placeholder_small).into(displayImageView)
        }

        if (currentUser == existingAccountUser) {
            welcomeTv.text = getString(R.string.welcome_back_to_tip_kasakasa)
            messageTv.text = getString((R.string.restore_wallet_with_phrase))
            createWalletBtn.text = getString(R.string.restore_wallet)
        }
    }

    private fun navigateToNextScreen() {
        if (currentUser == existingAccountUser) {
            navigateToRestoreWallet()
        } else {
            navigateToRecoveryPhrase()
        }
    }

    private fun navigateToRestoreWallet() {
        val intent = Intent(this, RestoreAccountActivity::class.java)
        intent.putExtra(AppConstants.EXTRA_EXISTING_ACCOUNT_USER, existingAccountUser)
        startActivity(intent)
    }

    private fun navigateToRecoveryPhrase() {
        val intent = Intent(this, RecoveryPhraseActivity::class.java)
        startActivity(intent)
    }
}
