package io.tipblockchain.kasakasa.ui.mainapp.confirmtransfer

import android.arch.lifecycle.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.blockchain.eth.Web3Bridge
import io.tipblockchain.kasakasa.data.db.entity.Transaction
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.data.db.repository.TransactionRepository
import io.tipblockchain.kasakasa.data.db.repository.UserRepository
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository
import io.tipblockchain.kasakasa.data.responses.PendingTransaction
import io.tipblockchain.kasakasa.networking.EthGasStationService
import io.tipblockchain.kasakasa.utils.TextUtils
import org.web3j.crypto.CipherException
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.utils.Convert
import java.lang.Error
import java.math.BigDecimal
import java.math.BigInteger

class ConfirmTransferPresenter: ConfirmTransfer.Presenter {

    lateinit var walletRepository: WalletRepository
    lateinit var userRepository: UserRepository
    lateinit var txRepository: TransactionRepository
    lateinit var web3Bridge: Web3Bridge
    lateinit var gasService: EthGasStationService
    private var gasDisposable: Disposable? = null
    private var txDisposable: Disposable? = null
    override var view: ConfirmTransfer.View? = null

    override fun attach(view: ConfirmTransfer.View) {
        super.attach(view)
        web3Bridge = Web3Bridge()
        walletRepository = WalletRepository.instance
        userRepository = UserRepository.instance
        txRepository = TransactionRepository.instance
        gasService = EthGasStationService.instance
    }

    override fun detach() {
        txDisposable?.dispose()
        gasDisposable?.dispose()

        super.detach()
    }

    override fun validateTransaction(transaction: PendingTransaction) {
        // check that 0 < value < wallet.balance
        // check that to is valid
        // check that if username, address = user.address
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
        val walletRequest = walletRepository.findWalletForAddressAndCurrency(transaction.from, transaction.currency)

        txDisposable = walletRequest.flatMap { wallet ->
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
                        return@flatMap txRepository.postTransaction(pendingTransaction = transaction, txrReceipt = TransactionReceipt())
                    } else {
                        AndroidSchedulers.mainThread().scheduleDirect {
                            view?.onUnhandledError()
                        }
                        return@flatMap null
                    }
                }
                return@flatMap null
            } catch (e: CipherException) {
                AndroidSchedulers.mainThread().scheduleDirect {
                    view?.onTransactionError(e)
                }
                return@flatMap null
            }
        }
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe ({tx ->
                    if (tx != null) {
                        txRepository.insert(tx)
                        AndroidSchedulers.mainThread().scheduleDirect {
                            view?.onTransactionPosted()
                        }
                    } else {
                        view?.onUnhandledError()
                    }
                }, {
                    view?.onTransactionError(it)
                })
//        val observer = walletRepository.findWalletForAddressAndCurrency(transaction.from, transaction.currency).observe(view!!, Observer { wallet ->
//            if (wallet == null) {
//                view?.onInvalidTransactionError(Error("Error loading your wallet."))
//                return@Observer
//            }
//
//            Schedulers.io().scheduleDirect {
//                try {
//                    val credentials = web3Bridge.loadCredentialsForWalletWithPassword(wallet, password)
//                    var txReceipt: TransactionReceipt? = null
//                    if (credentials != null) {
//                        when (transaction.currency) {
//                            Currency.TIP -> txReceipt = web3Bridge.sendTipTransaction(transaction.to, transaction.value, credentials)
//                            Currency.ETH -> txReceipt = web3Bridge.sendEthTransaction(transaction.to, transaction.value, credentials)
//                        }
//                        if (txReceipt != null) {
//                            AndroidSchedulers.mainThread().scheduleDirect {
//                                view?.onTransactionSent()
//                            }
//                        } else {
//                            AndroidSchedulers.mainThread().scheduleDirect {
//                                view?.onTransactionError(Error("Transaction failed."))
//                            }
//                        }
//                    }
//                } catch (e: CipherException) {
//                    AndroidSchedulers.mainThread().scheduleDirect {
//                        view?.onTransactionError(e)
//                    }
//                }
//            }
//        })
    }

    override fun getTransactionFee(transaction: PendingTransaction) {
        gasDisposable?.dispose()
        gasDisposable = gasService.getGasInfo().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe ({gasInfo ->
            var gasPriceInWei = web3Bridge.getGasPrice()
            if (gasInfo != null) {
                val infoGasPrice = Convert.toWei(BigDecimal.valueOf(gasInfo.average/10), Convert.Unit.GWEI).toBigInteger()
                gasPriceInWei = gasPriceInWei.min(infoGasPrice)
                if (gasPriceInWei == BigInteger.ZERO) {
                    gasPriceInWei = web3Bridge.getGasPrice()
                }
            }
            this.calculateTxFee(gasPriceInWei)
        }, {
            var gasPriceInWei = web3Bridge.getGasPrice()
            this.calculateTxFee(gasPriceInWei)
        })
    }

    private fun calculateTxFee(gasPrice: BigInteger) {
        val gasLimit = web3Bridge.getGasLimit()
        val gas = gasLimit.multiply(gasPrice)
        val txFee = Convert.fromWei(gas.toBigDecimal(), Convert.Unit.ETHER)
        view?.onTransactionFeeCalculated(txFee)
    }
}