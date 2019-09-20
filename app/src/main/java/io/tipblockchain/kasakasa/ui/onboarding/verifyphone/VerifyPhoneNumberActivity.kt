package io.tipblockchain.kasakasa.ui.onboarding.verifyphone

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.responses.PendingSignup
import io.tipblockchain.kasakasa.data.responses.PhoneVerificationRequest
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.onboarding.demoaccount.DemoAccountActivity
import io.tipblockchain.kasakasa.ui.onboarding.recovery.RecoveryPhraseActivity
import kotlinx.android.synthetic.main.activity_enter_phone_number.*
import kotlinx.android.synthetic.main.activity_enter_phone_number.form
import kotlinx.android.synthetic.main.activity_enter_phone_number.progressBar
import kotlinx.android.synthetic.main.activity_enter_phone_number.verifyPhoneBtn
import kotlinx.android.synthetic.main.activity_verify_phone_number.*

class VerifyPhoneNumberActivity : BaseActivity(), VerifyPhoneNumber.View {

    private var presenter: VerifyPhoneNumber.Presenter? = null
    private var verificationRequest: PhoneVerificationRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone_number)
        presenter = VerifyPhoneNumberPresenter()
        presenter?.attach(this)

        verificationRequest = intent.getSerializableExtra(AppConstants.EXTRA_PHONE_NUMBER) as PhoneVerificationRequest?
        if (verificationRequest == null) {
            finish()
        }

        verifyPhoneBtn.setOnClickListener { verifyPhoneNumber() }
    }

    override fun onResume() {
        super.onResume()
        verifyPhoneBtn.isEnabled = true
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detach()
    }

    override fun onPhoneVerificationError(error: Throwable) {
        showProgress(false)
        showMessage(getString(R.string.error_verifying_phone, error.localizedMessage))
        verifyPhoneBtn.isEnabled = true
    }

    override fun onPhoneVerifiedWithExistingAccount(account: User) {
        showProgress(false)
        navigateToExistingAccount(account)
    }

    override fun onPhoneVerifiedWithPendingAccount(pendingAccount: PendingSignup) {
        showProgress(false)
        navigateToRecoveryPhrase()
    }

    override fun onPhoneVerifiedWithPendingAndDemoAccount(pendingAccount: PendingSignup, demoAccount: User) {
        showProgress(false)
        navigateToDemoAccount(demoAccount)
    }

    override fun onUnknownError() {
        showProgress(false)
        showMessage(getString(R.string.error_verifying_phone, getString(R.string.error_verifying_phone)))
        verifyPhoneBtn.isEnabled = true
    }

    private fun verifyPhoneNumber() {
        verifyPhoneBtn.isEnabled = false
        verificationRequest!!.verificationCode = verificationCodeTv.text.toString()
        presenter?.verifyPhoneNumber(verificationRequest!!)
    }

    private fun navigateToDemoAccount(account: User) {
        val intent = Intent(this, DemoAccountActivity::class.java)
        intent.putExtra(AppConstants.EXTRA_DEMO_ACCOUNT_USER, account)
        startActivity(intent)
    }

    private fun navigateToExistingAccount(account: User) {
        val intent = Intent(this, DemoAccountActivity::class.java)
        intent.putExtra(AppConstants.EXTRA_EXISTING_ACCOUNT_USER, account)
        startActivity(intent)
    }

    private fun navigateToRecoveryPhrase() {
        val intent = Intent(this, RecoveryPhraseActivity::class.java)
        startActivity(intent)
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        form.visibility = if (show) View.GONE else View.VISIBLE
        form.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        form.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        progressBar.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        progressBar.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }
}
