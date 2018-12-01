package io.tipblockchain.kasakasa.ui.mainapp.confirmtransfer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.arch.lifecycle.Observer
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.EditText
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.data.db.repository.UserRepository
import io.tipblockchain.kasakasa.data.responses.PendingTransaction
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.mainapp.TransactionConfirmedActivity

import kotlinx.android.synthetic.main.activity_confirm_transaction.*
import java.lang.Error
import java.math.BigDecimal

class ConfirmTransferActivity : BaseActivity(), ConfirmTransfer.View {

    private var pendingTransaction: PendingTransaction? = null
    private var presenter: ConfirmTransfer.Presenter? = null
    private var userRepository = UserRepository.instance
    private var transactionSent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_transaction)

        presenter = ConfirmTransferPresenter()
        presenter?.attach(this)
        pendingTransaction = intent.getSerializableExtra(AppConstants.EXTRA_TRANSACTION) as PendingTransaction
        Log.d(LOG_TAG, "tx = $pendingTransaction")
        presenter?.validateTransaction(pendingTransaction!!)
        presenter?.getTransactionFee(pendingTransaction!!)

        if (pendingTransaction?.currency == Currency.ETH) {
            additionalTxFeeTv.visibility = View.GONE
        } else {
            totalAmountLabelTv.visibility = View.GONE
            totalAmountValueTv.visibility = View.GONE
            additionalTxFeeTv.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        presenter?.detach()
        super.onDestroy()
    }

    override fun onTransactionValidated() {
        updateViewWithTransaction()
        confirmBtn.setOnClickListener { showEnterPasswordDialog() }
    }

    override fun onTransactionFeeCalculated(txFee: BigDecimal) {
        transactionFeeTv.text = getString(R.string.plus_amount_and_currency, txFee, "ETH")
        if (pendingTransaction?.currency == Currency.ETH) {
            additionalTxFeeTv.visibility = View.GONE
            val totalAmount = pendingTransaction?.value?.plus(txFee)
            totalAmountValueTv.text = getString(R.string.amount_and_currency, totalAmount, pendingTransaction?.currency?.name)
        } else {
            additionalTxFeeTv.visibility = View.VISIBLE
            additionalTxFeeTv.text = getString(R.string.plus_amount_and_currency, txFee, "ETH")
            totalAmountLabelTv.visibility = View.GONE
            totalAmountValueTv.visibility = View.GONE
            additionalTxFeeTv.visibility = View.GONE
        }
    }

    override fun onInvalidTransactionError(error: Throwable) {
        showOkDialog(getString(R.string.invalid_transaction, error.localizedMessage), DialogInterface.OnClickListener { dialog, which ->
            finish()
        })

    }

    override fun onInvalidPasswordError() {
        showOkDialog(getString(R.string.could_not_unlock_wallet))
    }

    override fun onTransactionSent() {
        transactionSent = true
        navigateToTransactionConfirmed()
    }

    override fun onTransactionPosted() {
        showProgress(false)
        if (transactionSent) {
            navigateToTransactionConfirmed()
        }
    }

    override fun onUnhandledError() {
        confirmBtn.isEnabled = true
        this.onTransactionError(Error(getString(R.string.unknown_error)))
    }

    override fun onTransactionError(error: Throwable) {
        showProgress(false)
        confirmBtn.isEnabled = true
        // error might have been triggered after tx was sent, but during posting to server
        if (!transactionSent) {
            showOkDialog(getString(R.string.error_sending_transaction, error.localizedMessage))
        } else {
            navigateToTransactionConfirmed()
        }
    }

    private fun updateViewWithTransaction() {
        if (pendingTransaction == null) {
            return
        }

        if (pendingTransaction!!.toUsername != null) {
            toValueTv.text = pendingTransaction!!.toUsername
            userRepository.findUserById(pendingTransaction!!.toUsername!!).observe(this, Observer {

            })
        } else {
            toValueTv.text = pendingTransaction!!.to
        }

        fromValueTv.text = pendingTransaction!!.fromUsername
        transactionValueTv.text = getString(R.string.amount_and_currency, pendingTransaction!!.value, pendingTransaction!!.currency.name)
        totalAmountValueTv.text = getString(R.string.amount_and_currency, pendingTransaction!!.value, pendingTransaction!!.currency.name)
    }

    private fun navigateToTransactionConfirmed() {
        val intent = Intent(this, TransactionConfirmedActivity::class.java)
        startActivity(intent)
    }

    private fun showEnterPasswordDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_confirm_password, null)
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle(getString(R.string.unlock_wallet))
        alertDialog.setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_secure))
        alertDialog.setCancelable(false)

        val passwordView =  view.findViewById(R.id.passwordTv) as EditText

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.send)) { _, _ ->
            val password = passwordView.text.toString()
            sendTransaction(password)
        }

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }

        alertDialog.setView(view)
        alertDialog.show()
    }

    private fun sendTransaction(password: String) {
        showProgress(true)
        confirmBtn.isEnabled = false
        presenter?.sendTransactionAsync(pendingTransaction!!, password)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean = true) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        confirmBtn.isEnabled = show
        contentConstraingLayout.visibility = if (show) View.GONE else View.VISIBLE
        contentConstraingLayout.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        contentConstraingLayout.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })

        progressLinearLayout.visibility = if (show) View.VISIBLE else View.GONE
        progressLinearLayout.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        progressLinearLayout.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }

}
