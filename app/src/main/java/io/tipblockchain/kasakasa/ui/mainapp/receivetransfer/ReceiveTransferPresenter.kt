package io.tipblockchain.kasakasa.ui.mainapp.receivetransfer

import android.arch.lifecycle.Observer
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository

class ReceiveTransferPresenter: ReceiveTransfer.Presenter {

    lateinit var walletRepository: WalletRepository

    override fun attach(view: ReceiveTransfer.View) {
        super.attach(view)
        walletRepository = WalletRepository.instance
    }

    override fun loadWallet() {
        walletRepository.findWalletForCurrency(Currency.TIP).observe(view!!, Observer {
            if (it != null) {
                view?.showWallet(it.address)
            }
        })
    }

    override var view: ReceiveTransfer.View? = null
}