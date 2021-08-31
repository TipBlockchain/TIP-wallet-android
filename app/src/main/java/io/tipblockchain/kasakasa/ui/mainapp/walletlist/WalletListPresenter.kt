package io.tipblockchain.kasakasa.ui.mainapp.walletlist

import android.arch.lifecycle.Observer
import android.util.Log
import io.tipblockchain.kasakasa.crypto.EthProcessor
import io.tipblockchain.kasakasa.crypto.TipProcessor
import io.tipblockchain.kasakasa.crypto.TransactionProcessor
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository
import io.tipblockchain.kasakasa.ui.BasePresenter
import org.web3j.utils.Convert
import java.math.BigInteger
import java.util.*

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
                for (wallet in wallets) {
                    fetchBalance(wallet)
                }
            }
        })
    }

    private fun fetchBalance(wallet: Wallet) {
        var balanceChanged = false
        val currency = Currency.valueOf(wallet.currency)
        try {
            var processor: TransactionProcessor? = null
            processor = if (currency == Currency.TIP) TipProcessor(wallet) else EthProcessor()
            val balance = processor?.getBalance(wallet.address) ?: BigInteger.ZERO
            val balanceInEth =  Convert.fromWei(balance.toBigDecimal(), Convert.Unit.ETHER)
            if (balance != null) {
                if (balance != wallet.balance) {
                    wallet.balance = balance
                    wallet.lastSynced = Date()
                    walletRepository.update(wallet)
                }
                view?.onBalanceFetched(wallet.address, currency, balanceInEth)
            } else {
                view?.onBalanceFetchError(wallet.address, currency)
            }
        } catch (err: Throwable) {
            view?.onBalanceFetchError(wallet.address, currency = currency)
        }
    }
}