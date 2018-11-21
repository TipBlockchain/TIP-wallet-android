package io.tipblockchain.kasakasa.ui.onboarding.verifyphone

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.data.responses.PhoneVerificationRequest
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.onboarding.password.ChoosePasswordActivity
import io.tipblockchain.kasakasa.ui.onboarding.recovery.RecoveryPhraseActivity
import kotlinx.android.synthetic.main.activity_enter_phone_number.*

class VerifyPhoneNumberActivity : BaseActivity(), VerifyPhoneNumber.View {

    private var presenter: VerifyPhoneNumber.Presenter? = null
    private var verificationRequest: PhoneVerificationRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone_number)
        presenter = VerifyPhoneNumberPresenter()
        presenter?.attach(this)

        verificationRequest = intent.getSerializableExtra("phone") as PhoneVerificationRequest
        if (verificationRequest == null) {
            finish()
        }

        verifyPhoneBtn.setOnClickListener { verifyPhoneNumber() }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detach()
    }

    override fun onPhoneNumberVerified() {
        navigateToChoosePassword()
    }

    override fun onPhoneVerificationError(error: Throwable) {
        showProgress(false)
        showMessage(getString(R.string.error_verifying_phone, error.localizedMessage))
    }

    override fun onUnknownError() {
        showProgress(false)
        showMessage(getString(R.string.error_verifying_phone, getString(R.string.error_verifying_phone)))
    }

    private fun verifyPhoneNumber() {
        verificationRequest!!.verificationCode = verificationCodeTv.text.toString()
        presenter?.verifyPhoneNumber(verificationRequest!!)
    }

    private fun navigateToChoosePassword() {
        val intent = Intent(this, ChoosePasswordActivity::class.java)
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
