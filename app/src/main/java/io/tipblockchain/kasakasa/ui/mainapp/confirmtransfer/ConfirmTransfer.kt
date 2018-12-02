package io.tipblockchain.kasakasa.ui.mainapp.confirmtransfer

import android.arch.lifecycle.LifecycleOwner
import io.tipblockchain.kasakasa.data.responses.PendingTransaction
import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView
import java.math.BigDecimal

interface ConfirmTransfer {

    interface View: BaseView, LifecycleOwner {
        fun onTransactionFeeCalculated(txFee: BigDecimal)
        fun onTransactionValidated()
        fun onInvalidTransactionError(error: Throwable)
        fun onInvalidPasswordError()
        fun onTransactionSent()
        fun onTransactionPosted()
        fun onTransactionError(error: Throwable)
        fun onUnhandledError()
    }

    interface Presenter: BasePresenter<View> {
        fun getTransactionFee(transaction: PendingTransaction)
        fun validateTransaction(transaction: PendingTransaction)
        fun sendTransactionWithPassword(transaction: PendingTransaction, password: String)
        fun sendTransactionAsync(transaction: PendingTransaction, password: String)
    }
}