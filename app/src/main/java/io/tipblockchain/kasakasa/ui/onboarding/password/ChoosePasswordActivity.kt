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
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil

import kotlinx.android.synthetic.main.activity_choose_password.*

import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.databinding.ActivityChoosePasswordBinding
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.mainapp.MainTabActivity
import io.tipblockchain.kasakasa.ui.onboarding.profile.OnboardingUserProfileActivity
import io.tipblockchain.kasakasa.ui.onboarding.recovery.RecoveryPhraseActivity

/**
 * A login screen that offers login via email/password.
 */
class ChoosePasswordActivity : BaseActivity(), ChoosePassword.View {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private lateinit var viewModel: ChoosePasswordViewModel
    private var presenter: ChoosePassword.Presenter? = null
    private var recoveryPhrase: String? = null
    private var existingUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityChoosePasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_choose_password)

        recoveryPhrase = intent.getStringExtra(AppConstants.EXTRA_RECOVERY_PHRASE)
        existingUser = intent.getSerializableExtra(AppConstants.EXTRA_EXISTING_ACCOUNT_USER) as User?

        // Set up the password form.
        passwordTv.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                confirmPasswordTv.requestFocus()
                return@OnEditorActionListener true
            }
            false
        })
        confirmPasswordTv.setOnEditorActionListener( TextView.OnEditorActionListener{ _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE) {
                checkPassword()
                return@OnEditorActionListener true
            }
            false
        })

        presenter = ChoosePasswordPresenter()
        presenter?.attach(this)
        presenter?.setExistingUser(existingUser)

        nextBtn.setOnClickListener { checkPassword() }
        viewModel = getViewModel()
        binding.viewModel = viewModel
    }

    override fun onDestroy() {
        presenter?.detach()
        super.onDestroy()
    }

    override fun onWalletRestored() {
        showProgress(false)
        showOkDialog(title = getString(R.string.hooray), message =  getString(R.string.message_wallet_restored), onClickListener = object: DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                navigateToMainApp()
            }
        })
    }

    override fun onWalletCreated() {
        showProgress(false)
        navigateToUserProfile()
    }

    override fun onWalletNotMatchingExistingError() {
        showProgress(false)
        if (existingUser != null) {
            showOkDialog(title = getString(R.string.sorry), message = getString(R.string.error_address_not_matching_account, existingUser!!.username), onClickListener = object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    finish()
                }
            })
        }
    }

    override fun onWalletCreationError(error: Throwable) {
        showProgress(false)
        showMessage(getString(R.string.generic_error_with_param, error.localizedMessage))
    }

    private fun getViewModel() = ViewModelProviders.of(this).get(ChoosePasswordViewModel::class.java)

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun checkPassword() {
        // Reset errors.
        showProgress(true)
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
            showProgress(false)
            focusView?.requestFocus()
        } else {
            // Show a progress spinner, and proceed to next screen
//            showProgress(true)
            presenter?.generateWalletFromMnemonicAndPassword(mnemonic = recoveryPhrase!!, password = viewModel.password)
//            presenter?.checkAndGenerateWallet(mnemonic = recoveryPhrase!!, password = viewModel.password)
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
        nextBtn.isEnabled = !show
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        passwordForm.visibility = if (show) View.GONE else View.VISIBLE
        passwordForm.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        passwordForm.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

        progressLayout.visibility = if (show) View.VISIBLE else View.GONE
        progressLayout.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        progressLayout.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }

    fun navigateToUserProfile() {
        val intent = Intent(this, OnboardingUserProfileActivity::class.java)
        intent.putExtra(AppConstants.EXTRA_PASSWORD, viewModel.password)
        startActivity(intent)
    }

    fun navigateToMainApp() {
        val intent = Intent(this, MainTabActivity::class.java)
        startActivity(intent)
    }
}
