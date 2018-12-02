package io.tipblockchain.kasakasa.ui.mainapp.receivetransfer

import android.arch.lifecycle.LifecycleOwner
import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView

interface ReceiveTransfer {
    interface View: BaseView, LifecycleOwner {
        fun showWallet(address: String)
        fun copyAddress()
    }

    interface Presenter: BasePresenter<View> {
        fun loadWallet()
    }
}