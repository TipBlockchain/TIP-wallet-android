package io.tipblockchain.kasakasa.ui.mainapp.confirmtransfer

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.EditText
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.data.db.repository.UserRepository
import io.tipblockchain.kasakasa.data.responses.PendingTransaction
import io.tipblockchain.kasakasa.ui.BaseActivity
import io.tipblockchain.kasakasa.ui.mainapp.TransactionConfirmedActivity

import kotlinx.android.synthetic.main.activity_confirm_transaction.*
import java.math.BigDecimal

class ConfirmTransferActivity : BaseActivity(), ConfirmTransfer.View {

    private var pendingTransaction: PendingTransaction? = null
    private var presenter: ConfirmTransfer.Presenter? = null
    private var userRepository = UserRepository.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_transaction)

        presenter = ConfirmTransferPresenter()
        presenter?.attach(this)
        pendingTransaction = intent.getSerializableExtra("transaction") as PendingTransaction
        Log.d(LOG_TAG, "tx = $pendingTransaction")
        presenter?.validateTransaction(pendingTransaction!!)
        presenter?.getTransactionFee(pendingTransaction!!)
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
        if (pendingTransaction?.currency == Currency.ETH) {
            additionalTxFeeTv.visibility = View.GONE
            val totalAmount = pendingTransaction?.value?.plus(txFee)
            totalAmountValueTv.text = getString(R.string.amount_and_currency, totalAmount, pendingTransaction?.currency?.name)
        } else {
            additionalTxFeeTv.visibility = View.VISIBLE
            additionalTxFeeTv.text = getString(R.string.plus_amount_and_currency, txFee, "ETH")
        }
    }

    override fun onInvalidTransactionError(error: Throwable) {
        showOkDialog(getString(R.string.invalid_transaction, error.localizedMessage))
    }

    override fun onInvalidPasswordError() {
        showOkDialog(getString(R.string.could_not_unlock_wallet))
    }

    override fun onTransactionSent() {
        navigateToTransactionConfirmed()
    }

    override fun onTransactionError(error: Throwable) {
        showOkDialog(getString(R.string.error_sending_transaction, error.localizedMessage))
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
        intent.putExtra("keyIdentifier", "value")
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
            presenter?.sendTransactionWithPassword(pendingTransaction!!, password)
        }


        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }

        alertDialog.setView(view)
        alertDialog.show()
    }

}
