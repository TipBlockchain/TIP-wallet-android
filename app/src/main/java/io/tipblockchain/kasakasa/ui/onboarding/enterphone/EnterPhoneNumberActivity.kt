package io.tipblockchain.kasakasa.ui.onboarding.enterphone

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.view.View

import android.content.Intent
import android.os.Handler
import com.heetch.countrypicker.Country
import com.heetch.countrypicker.CountryPickerCallbacks
import com.heetch.countrypicker.CountryPickerDialog
import com.heetch.countrypicker.Utils
import com.squareup.picasso.Picasso
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.data.responses.PhoneVerificationRequest
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.onboarding.verifyphone.VerifyPhoneNumberActivity

import kotlinx.android.synthetic.main.activity_enter_phone_number.*
import java.util.*


/**
 * A login screen that offers login via email/password.
 */
class EnterPhoneNumberActivity : BaseActivity(), EnterPhone.View {

    private var countryPicker: CountryPickerDialog? = null
    private var presenter: EnterPhone.Presenter? = null
    private var verificationRequest: PhoneVerificationRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_phone_number)
        // Set up the login form.

        presenter = EnterPhoneNumberPresenter()
        presenter?.attach(this)

        countryCodeTv.setText(getString(R.string.placeholder_country_code))

        setupCountryPicker()
        verifyPhoneBtn.setOnClickListener { startPhoneVerification() }
        selectCountryTv.setOnClickListener { showCountryPicker() }
        flagIv.setOnClickListener { showCountryPicker() }
    }


    override fun onResume() {
        super.onResume()
        verifyPhoneBtn.isEnabled = true
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detach()
        countryPicker?.dismiss()
    }

    override fun onEmptyPhoneNumberError() {
        showProgress(false)
        verifyPhoneBtn.isEnabled = true
        verificationCodeTv.error = getString(R.string.error_creating_account)
    }

    override fun onInvalidPhoneNumberError() {
        showProgress(false)
        verifyPhoneBtn.isEnabled = true
        verificationCodeTv.error = getString(R.string.error_creating_account)
    }

    override fun onVerificationError(error: Throwable) {
        showProgress(false)
        verifyPhoneBtn.isEnabled = true
        verificationCodeTv.error = getString(R.string.error_sending_verification_text, error.localizedMessage)
    }

    override fun onVerificationStarted() {
        showProgress(false)
        showMessage(getString(R.string.verification_started))
        Handler().postDelayed({
            navigateToVerifyPhoneNumber()
        }, 1000)
    }

    private fun setupCountryPicker() {
        countryPicker = CountryPickerDialog(this, PhoneNumberCountryPickerCallbacks(),null, true)
    }

    private fun showCountryPicker() {
        countryPicker?.show()
    }

    private fun startPhoneVerification() {
        showProgress(true)
        verifyPhoneBtn.isEnabled = false
        verificationRequest = PhoneVerificationRequest(countryCode = countryCodeTv.text.toString(), phoneNumber = verificationCodeTv.text.toString())
        presenter?.validatePhoneNumber(verificationRequest!!)
    }

    inner class PhoneNumberCountryPickerCallbacks: CountryPickerCallbacks {

        override fun onCountrySelected(country: Country, flagResId: Int) {
            flagIv.visibility = View.VISIBLE
            Picasso.get().load(Utils.getMipmapResId(this@EnterPhoneNumberActivity, "${country.getIsoCode()}_flag")).into(flagIv)

            selectCountryTv.text = getCountryName(country)
            countryCodeTv.setText(getString(R.string.plus_plus_country_code, country.getDialingCode()))
        }
    }

    private fun getCountryName(country: Country): String {
        return Locale(this.resources.getConfiguration().locale.getLanguage(),
                country.getIsoCode()).getDisplayCountry()
    }

    fun navigateToVerifyPhoneNumber() {
        val intent = Intent(this, VerifyPhoneNumberActivity::class.java)
        intent.putExtra(AppConstants.EXTRA_PHONE_NUMBER, verificationRequest)
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
