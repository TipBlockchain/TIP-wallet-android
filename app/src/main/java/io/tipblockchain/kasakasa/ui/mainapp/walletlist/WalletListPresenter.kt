package io.tipblockchain.kasakasa.ui.mainapp.walletlist

import android.arch.lifecycle.Observer
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository
import io.tipblockchain.kasakasa.ui.BasePresenter

class WalletListPresenter: WalletList.Presenter {
    private val walletRepository = WalletRepository.instance
    override var view: WalletList.View? = null

    override fun attach(view: WalletList.View) {
        super.attach(view)

    }
    override fun loadWallets() {
        walletRepository.allWallets().observe(view!!, Observer {wallets ->

            if (wallets != null) {
                view?.showWallets(wallets)
            }
        })

    }
}