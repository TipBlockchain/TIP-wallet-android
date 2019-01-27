package io.tipblockchain.kasakasa.ui.mainapp.confirmtransfer

import android.arch.lifecycle.Observer
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.blockchain.eth.Web3Bridge
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

    private val logTag = javaClass.name

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

    override fun sendTransactionAsync(transaction: PendingTransaction, password: String, gasPriceInGwei: Int) {
        val walletRequest = walletRepository.findWalletForAddressAndCurrency(transaction.from, transaction.currency)
        txDisposable?.dispose()
        txDisposable = walletRequest.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe({ wallet ->
                    if (wallet != null) {
                        val credentials = web3Bridge.loadCredentialsForWalletWithPassword(wallet, password)
                        if (credentials != null) {
                            txRepository.sendTransaction(transaction, credentials, gasPriceInGwei) {txr: TransactionReceipt?, err: Throwable? ->
                                AndroidSchedulers.mainThread().scheduleDirect {
                                    if (err != null) {
//                                        view?.onTransactionError(err ?: Error("An error occurred while sending transaction"))
                                        // TODO: Add global notifier to notify of errors
                                        Log.e(logTag, "Error sending transaction")
                                        Log.e(logTag, err.stackTrace.toString())
                                    }
                                }
                            }
                            view?.onTransactionSent()
                        }
                    }
                }, {
                    AndroidSchedulers.mainThread().scheduleDirect {
                        view?.onTransactionError(it)
                    }
                })
    }

    override fun sendTransactionWithPassword(transaction: PendingTransaction, password: String) {
        val walletRequest = walletRepository.findWalletForAddressAndCurrency(transaction.from, transaction.currency)

        txDisposable = walletRequest.flatMap { wallet ->
            try {
                val credentials = web3Bridge.loadCredentialsForWalletWithPassword(wallet, password)
                var txReceipt: TransactionReceipt? = null
                if (credentials != null) {
                    when (transaction.currency) {
                        Currency.TIP -> txReceipt = web3Bridge.sendTipTransactionAsync(transaction.to, transaction.value, credentials)
                        Currency.ETH -> txReceipt = web3Bridge.sendEthTransactionAsync(transaction.to, transaction.value, credentials)
                    }
                    if (txReceipt != null) {
                        AndroidSchedulers.mainThread().scheduleDirect {
                            view?.onTransactionSent()
                        }
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
            } catch (e: InterruptedException) {
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
                    AndroidSchedulers.mainThread().scheduleDirect {
//                        view?.onTransactionError(it) // decryption error sent from observer
                    }
                })
    }



    private fun calculateTxFee(gasPrice: BigInteger) {
        val gasLimit = web3Bridge.getGasLimit()
        val gas = gasLimit.multiply(gasPrice)
        val txFee = Convert.fromWei(gas.toBigDecimal(), Convert.Unit.ETHER)
        view?.onTransactionFeeCalculated(txFee)
    }
}