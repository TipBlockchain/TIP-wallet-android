package io.tipblockchain.kasakasa.ui.mainapp.confirmtransfer

import android.arch.lifecycle.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.blockchain.eth.Web3Bridge
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.data.db.repository.UserRepository
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository
import io.tipblockchain.kasakasa.data.responses.PendingTransaction
import io.tipblockchain.kasakasa.utils.TextUtils
import org.web3j.crypto.CipherException
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.utils.Convert
import java.lang.Error
import java.math.BigInteger

class ConfirmTransferPresenter: ConfirmTransfer.Presenter {

    lateinit var walletRepository: WalletRepository
    lateinit var userRepository: UserRepository
    lateinit var web3Bridge: Web3Bridge
    override var view: ConfirmTransfer.View? = null

    override fun attach(view: ConfirmTransfer.View) {
        super.attach(view)
        web3Bridge = Web3Bridge()
        walletRepository = WalletRepository.instance
        userRepository = UserRepository.instance
    }

    override fun validateTransaction(transaction: PendingTransaction) {
        // check that 0 < value < wallet.balance
        // check that to is valid
        // check that if username, address = user.address
        var walletBalance: BigInteger? = BigInteger.ZERO
        when (transaction.currency) {
            Currency.TIP -> walletBalance = web3Bridge.getTipBalanceAsync(transaction.from)
            Currency.ETH -> walletBalance = web3Bridge.getEthBalanceAsync(transaction.from)
        }
        if (walletBalance == null) walletBalance = BigInteger.ZERO
        val valueInWei = Convert.toWei(transaction.value, Convert.Unit.ETHER).toBigInteger()
        if (walletBalance!! < valueInWei || valueInWei < BigInteger.ZERO) {
            view?.onInvalidTransactionError(Error("Insufficient funds"))
            return
        }

        if (!TextUtils.isEthAddress(transaction.to)) {
            view?.onInvalidTransactionError(Error("The destination address is not valid"))
            return
        }

        if (transaction.toUsername != null) {
            userRepository.findUserByUsername(transaction.toUsername!!).observe(view!!, Observer { user ->
                if (user == null || user.address != transaction.to) {
                    view?.onInvalidTransactionError(Error("The destination address could not be verified."))
                } else {
                    view?.onTransactionValidated()
                }
            })
        } else {
            view?.onTransactionValidated()
        }
    }

    override fun sendTransactionWithPassword(transaction: PendingTransaction, password: String) {
        walletRepository.findWalletForAddressAndCurrency(transaction.from, transaction.currency).observe(view!!, Observer { wallet ->
            if (wallet == null) {
                view?.onInvalidTransactionError(Error("Error loading your wallet."))
                return@Observer
            }

            try {
                val credentials = web3Bridge.loadCredentialsForWalletWithPassword(wallet, password)
                var txReceipt: TransactionReceipt? = null
                if (credentials != null) {
                    when (transaction.currency) {
                        Currency.TIP -> txReceipt = web3Bridge.sendTipTransaction(transaction.to, transaction.value, credentials)
                        Currency.ETH -> txReceipt = web3Bridge.sendEthTransaction(transaction.to, transaction.value, credentials)
                    }
                    if (txReceipt != null) {
                        view?.onTransactionSent()
                    } else {
                        view?.onTransactionError(Error("Transaction failed."))
                    }
                }
            } catch (e: CipherException) {
                view?.onTransactionError(e)
            }
        })
    }

    override fun getTransactionFee(transaction: PendingTransaction) {
        val gasPrice = web3Bridge.getGasPrice()
        val gasLimit = web3Bridge.getGasLimit()
        val gas = gasLimit.multiply(gasPrice)
        val txFee = Convert.fromWei(gas.toBigDecimal(), Convert.Unit.ETHER)
        view?.onTransactionFeeCalculated(txFee)
    }
}