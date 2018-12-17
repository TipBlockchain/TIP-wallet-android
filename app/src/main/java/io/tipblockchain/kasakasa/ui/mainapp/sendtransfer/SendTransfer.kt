package io.tipblockchain.kasakasa.ui.mainapp.sendtransfer

import android.arch.lifecycle.LifecycleOwner
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.data.responses.PendingTransaction
import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView
import java.math.BigDecimal

interface SendTransfer {
    interface View: BaseView, LifecycleOwner {
        fun onUserNotFound(username: String)
        fun onInvalidRecipient()
        fun onInsufficientBalanceError()
        fun onInvalidTransactionValueError()
        fun onWalletError()
        fun onSendPendingTransaction(tx: PendingTransaction)
        fun onContactsFetched(list: List<User>)
        fun onContactsFetchError(error: Throwable)
    }

    interface Presenter: BasePresenter<View> {
        fun loadContactList()
        fun userSelected(user: User?, address: String)
        fun amountEntered(amount: BigDecimal)
        fun currencySelected(currency: Currency)
        fun validateTransfer(usernameOrAddress: String, value: String, currency: Currency, message: String)
        fun nextTapped()
    }
}