package io.tipblockchain.kasakasa.ui.mainapp.sendtransfer

import android.arch.lifecycle.LifecycleOwner
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView
import java.math.BigDecimal

interface SendTransfer {
    interface View: BaseView, LifecycleOwner {
        fun onContactsFetched(list: List<User>)
        fun onContactsFetchError(error: Throwable)
    }

    interface Presenter: BasePresenter<View> {
        fun fetchContactList()
        fun userSelected(user: User?, address: String)
        fun amountEntered(amount: BigDecimal)
        fun currencySelected(currency: Currency)
        fun nextTapped()
    }
}