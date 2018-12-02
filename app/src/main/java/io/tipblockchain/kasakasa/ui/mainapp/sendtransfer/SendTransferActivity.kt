package io.tipblockchain.kasakasa.ui.mainapp.sendtransfer

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.zxing.client.android.Intents
import com.google.zxing.integration.android.IntentIntegrator
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.data.responses.PendingTransaction
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.mainapp.confirmtransfer.ConfirmTransferActivity
import kotlinx.android.synthetic.main.activity_send_transfer.*

class SendTransferActivity : BaseActivity(), SendTransfer.View, AdapterView.OnItemSelectedListener {
    private val REQUEST_READ_CONTACTS = 0
    var presenter: SendTransferPresenter? = null
    var adapter: UserFilterAdapter? = null
    var selectedCurrency: Currency = Currency.TIP
    val scannerRequestCode = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_transfer)

        presenter = SendTransferPresenter()
        presenter?.attach(this)
        // Set up the login form.
        populateAutoComplete()
        adapter = UserFilterAdapter(this, listOf())
        recepientTv.setAdapter(adapter)

        recepientTv.threshold = 1
        presenter?.fetchContactList()
        nextButton.setOnClickListener { nextButtonClicked() }
        scanButton.setOnClickListener { showQRCodeScanner() }
        setupSpinner()
        var username = intent.getStringExtra(AppConstants.TRANSACTION_RECIPIENT)
        if (username  != null) {
            recepientTv.setText(username)
        }
    }

    override fun onDestroy() {
        presenter?.detach()
        super.onDestroy()
    }

    private fun populateAutoComplete() {
        if (!mayRequestContacts()) {
            return
        }
    }

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
                this,
                R.array.currency_options,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
            spinner.onItemSelectedListener = this
        }
    }

    private fun mayRequestContacts(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
            Snackbar.make(recepientTv, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok,
                            { requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_READ_CONTACTS) })
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_READ_CONTACTS)
        }
        return false
    }

    private fun showQRCodeScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt(getString(R.string.scan_address))
        integrator.setBeepEnabled(true)
        integrator.setBarcodeImageEnabled(true)
        integrator.setRequestCode(scannerRequestCode)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == scannerRequestCode) {
            if (data == null || !data.hasExtra(Intents.Scan.RESULT)) {
                showMessage(getString(R.string.scan_cancelled))
                return
            }

            val qrCode = data.getStringExtra(Intents.Scan.RESULT)
            if (qrCode.isEmpty()) {
                showMessage(getString(R.string.scan_cancelled))
                return
            }

            Log.d(LOG_TAG, "Adress scanned: $qrCode")
            recepientTv.setText(qrCode)
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete()
            }
        }
    }

    fun  navigateToConfirmWithTransaction(tx: PendingTransaction) {
        val intent = Intent(this, ConfirmTransferActivity::class.java)
        intent.putExtra(AppConstants.EXTRA_TRANSACTION, tx)
        startActivity(intent)
    }

    fun nextButtonClicked() {
        showProgress(true)
        validateInputs()
    }

    fun validateInputs() {
        val usernameOrAddress = recepientTv.text.toString()
        val amount = amountTv.text.toString()
        val message = messageTv.text.toString()

        presenter?.validateTransfer(usernameOrAddress = usernameOrAddress, value = amount, currency = selectedCurrency, message = message)
    }

    override fun onInvalidRecipient() {
        showProgress(false)
        showMessage("Recipient field is not valid. Please enter a valid TIP username or ETH address.")
    }

    override fun onInvalidTransactionValueError() {
        showProgress(false)
        showMessage(getString(R.string.invalid_transaction_value))
    }

    override fun onUserNotFound(username: String) {
        showProgress(false)
        showMessage("User $username not found in your contacts. You can only send transactions by username to users in your contact list. Please add $username to your contacts and try again")
    }

    override fun onWalletError() {
        showProgress(false)
        showMessage("Failed to load your wallet")
    }

    override fun onInsufficientBalanceError() {
        showProgress(false)
        showMessage("Insufficient balance")
    }

    override fun onSendPendingTransaction(tx: PendingTransaction) {
        showProgress(false)
        navigateToConfirmWithTransaction(tx)
    }

    override fun onContactsFetched(list: List<User>) {
        adapter?.setSuggestionList(list)
    }

    override fun onContactsFetchError(error: Throwable) {
        showMessage(error.localizedMessage)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (position) {
            0 -> selectedCurrency = Currency.TIP
            1 -> selectedCurrency = Currency.ETH
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

        form.visibility = if (show) View.GONE else View.VISIBLE
        form.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        form.visibility = if (show) View.GONE else View.VISIBLE
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

}
