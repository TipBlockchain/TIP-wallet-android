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
        fun onInsufficientEthBalanceError()
        fun onInsufficientBalanceError()
        fun onInvalidTransactionValueError()
        fun onWalletError()
        fun onSendPendingTransaction(tx: PendingTransaction)
        fun onBalanceFetched(balance: BigDecimal, currency: Currency)
        fun onContactsFetched(list: List<User>)
        fun onContactsFetchError(error: Throwable)
        fun onTransactionFeeCalculated(feeInEth: BigDecimal, gasPriceInGwei: Int)
    }

    interface Presenter: BasePresenter<View> {
        fun loadWallets()
        fun loadContactList()
        fun userSelected(user: User?, address: String)
        fun amountEntered(amount: BigDecimal)
        fun currencySelected(currency: Currency)
        fun validateTransfer(usernameOrAddress: String, value: BigDecimal, transactionFee: BigDecimal, currency: Currency, message: String)
        fun calculateTransactionFee(gasPrice: Int)
    }
}