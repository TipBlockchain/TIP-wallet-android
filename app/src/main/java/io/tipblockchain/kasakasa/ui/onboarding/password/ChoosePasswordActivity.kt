package io.tipblockchain.kasakasa.ui.onboarding.password

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.support.v7.app.AppCompatActivity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil

import kotlinx.android.synthetic.main.activity_choose_password.*

import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.databinding.ActivityChoosePasswordBinding
import io.tipblockchain.kasakasa.ui.onboarding.recovery.RecoveryPhraseActivity

/**
 * A login screen that offers login via email/password.
 */
class ChoosePasswordActivity : AppCompatActivity() {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    lateinit private var viewModel: ChoosePasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityChoosePasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_choose_password)

        // Set up the password form.
        passwordTv.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                checkPassword()
                return@OnEditorActionListener true
            }
            false
        })

        nextBtn.setOnClickListener { checkPassword() }
        viewModel = getViewModel()
        binding.viewModel = viewModel
    }

    private fun getViewModel() = ViewModelProviders.of(this).get(ChoosePasswordViewModel::class.java)

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun checkPassword() {
        // Reset errors.
        passwordTv.error = null
        confirmPasswordTv.error = null

        var cancel = false
        var focusView: View? = null

        viewModel.password = passwordTv.text.toString()
        viewModel.confirmedPassword = confirmPasswordTv.text.toString()

        // Check for a valid email address.
        if (viewModel.passwordIsEmpty()) {
            confirmPasswordTv.error = getString(R.string.error_field_required)
            focusView = confirmPasswordTv
            cancel = true
        }

        // Check for a valid password, if the user entered one.
        if (!viewModel.isPasswordValid()) {
            passwordTv.error = getString(R.string.error_invalid_password)
            focusView = passwordTv
            cancel = true
        }

        if (!viewModel.passwordsMatch()) {
            passwordTv.error = getString(R.string.error_passwords_dont_match)
            focusView = passwordTv
            cancel = true
        }

        if (cancel) {
            // There was an error; don't proceed and focus the first
            // form field with an error.
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and proceed to next screen
            showProgress(true)
            goToAccountBackup()
        }
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

        password_form.visibility = if (show) View.GONE else View.VISIBLE
        password_form.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        password_form.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

        login_progress.visibility = if (show) View.VISIBLE else View.GONE
        login_progress.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        login_progress.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }

    fun goToAccountBackup() {
        val intent = Intent(this, RecoveryPhraseActivity::class.java)
        intent.putExtra("password", viewModel.password)
        startActivity(intent)
    }
}
