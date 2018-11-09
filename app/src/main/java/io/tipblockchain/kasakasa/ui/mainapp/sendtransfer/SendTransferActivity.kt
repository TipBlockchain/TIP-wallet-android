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
import android.view.View
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.data.responses.PendingTransaction
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.mainapp.ConfirmTransferActivity
import io.tipblockchain.kasakasa.utils.TextUtils
import kotlinx.android.synthetic.main.activity_send_transfer.*

class SendTransferActivity : BaseActivity(), SendTransfer.View {
    private val REQUEST_READ_CONTACTS = 0
    var presenter: SendTransferPresenter? = null
    var adapter: UserFilterAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_transfer)

        presenter = SendTransferPresenter()
        presenter?.attach(this)
        // Set up the login form.
        populateAutoComplete()
        adapter = UserFilterAdapter(this, listOf())
        recepientTv.setAdapter(adapter)

        recepientTv.threshold = 2
        presenter?.fetchContactList()
        nextButton.setOnClickListener { nextButtonClicked() }
    }

    private fun populateAutoComplete() {
        if (!mayRequestContacts()) {
            return
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
        intent.putExtra("transaction", tx)
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

        presenter?.validateTransfer(usernameOrAddress = usernameOrAddress, value = amount, currency = Currency.ETH, message = message)
    }

    override fun onInvalidRecipient() {
        showProgress(false)
        showMessage("Recipient field is not valid. Please enter a valid TIP username or ETH address.")
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
