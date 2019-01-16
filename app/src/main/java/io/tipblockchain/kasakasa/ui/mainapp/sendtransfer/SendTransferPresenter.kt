package io.tipblockchain.kasakasa.ui.mainapp.sendtransfer

import android.arch.lifecycle.Observer
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.blockchain.eth.Web3Bridge
import io.tipblockchain.kasakasa.crypto.EthProcessor
import io.tipblockchain.kasakasa.crypto.TipProcessor
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.data.db.repository.UserRepository
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository
import io.tipblockchain.kasakasa.data.responses.PendingTransaction
import io.tipblockchain.kasakasa.networking.EthGasStationService
import io.tipblockchain.kasakasa.utils.TextUtils
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

class SendTransferPresenter: SendTransfer.Presenter {
    private val userRepository = UserRepository.instance
    private val walletRepository = WalletRepository.instance
    private val estimatedGas = 59_000L
    private lateinit var web3Bridge: Web3Bridge

    private var tipWallet: Wallet? = null
    private var ethWallet: Wallet? = null

    private val LOG_TAG = javaClass.name

    override fun attach(view: SendTransfer.View) {
        super.attach(view)
        web3Bridge = Web3Bridge()
    }

    override fun loadContactList() {
        userRepository.loadContactsFromDb().observe(view!!, object : Observer<List<User>> {
            override fun onChanged(contacts: List<User>?) {
                if (contacts != null) {
                    view?.onContactsFetched(contacts)
                }
            }
        })
    }

    override fun currencySelected(currency: Currency) {
        when (currency) {
            Currency.ETH -> fetchBalance(ethWallet)
            Currency.TIP -> fetchBalance(tipWallet)
        }
    }

    override fun userSelected(user: User?, address: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun amountEntered(amount: BigDecimal) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun calculateTransactionFee(gasPrice: Int) {
        var gasPriceInWei = Convert.toWei(gasPrice.toBigDecimal(), Convert.Unit.GWEI).toBigInteger()
        val calculatedTxFee = gasPriceInWei.times(estimatedGas.toBigInteger())
        val priceInEth = Convert.fromWei(calculatedTxFee.toBigDecimal(), Convert.Unit.ETHER)
        view?.onTransactionFeeCalculated(priceInEth, gasPrice)
    }

    override fun validateTransfer(usernameOrAddress: String, value: BigDecimal, transactionFee: BigDecimal, currency: Currency, message: String) {

        var wallet: Wallet? = null
        when (currency) {
            Currency.TIP -> wallet = tipWallet
            Currency.ETH -> wallet = ethWallet
        }

        if (wallet == null) {
            view?.onWalletError()
            return
        }
        if (!TextUtils.isEthAddress(usernameOrAddress) && !TextUtils.isUsername(usernameOrAddress)) {
            view?.onInvalidRecipient()
            return
        }

        // Check user balances are sufficient
        val txValueInWei = Convert.toWei(value, Convert.Unit.ETHER).toBigInteger()
        val txFeeInWei = Convert.toWei(transactionFee, Convert.Unit.ETHER).toBigInteger()
        val ethBalanceInWei = ethWallet!!.balance
        val accountBalance = wallet.balance

        if (currency == Currency.ETH && txValueInWei + txFeeInWei > accountBalance) {
            view?.onInsufficientBalanceError()
            return
        }

        if (currency == Currency.TIP && txFeeInWei > ethBalanceInWei) {
            view?.onInsufficientEthBalanceError()
            return
        }

        // Check that sufficient funds exist
        if (txValueInWei > accountBalance) {
            view?.onInsufficientBalanceError()
            return
        }

        var address: String?
        var username: String?

        if (TextUtils.isEthAddress(usernameOrAddress)) {
            address = usernameOrAddress
            val pending = PendingTransaction(
                    from = wallet.address,
                    fromUsername = UserRepository.currentUser!!.username,
                    to = address,
                    toUsername = null,
                    value = value,
                    currency = currency)
            calculateActualTxFee(pending, 21_000_000L.toBigInteger())
            view?.onSendPendingTransaction(tx = pending)
        } else if (TextUtils.isUsername(usernameOrAddress)) {
            username = usernameOrAddress
            userRepository.findUserByUsername(username).observe(view!!, Observer { user ->
                if (user == null) {
                    view?.onUserNotFound(username)
                } else {
                    if (!TextUtils.isEthAddress(user.address)) {
                        view?.onInvalidRecipient()
                        return@Observer
                    }
                    val pending = PendingTransaction(
                            from = wallet.address,
                            fromUsername = UserRepository.currentUser!!.username,
                            to = user.address,
                            toUsername = user.username,
                            value = value,
                            currency = currency
                    )
                    calculateActualTxFee(pending, 21_000_000L.toBigInteger())
                    view?.onSendPendingTransaction(tx = pending)
                }
            })
        } else {
            view?.onInvalidRecipient()
        }
    }

    private fun calculateActualTxFee(pendingTransaction: PendingTransaction, gasPrice: BigInteger) {

        Schedulers.io().scheduleDirect {
            val actualTransaction = web3Bridge.craeteTransaction(
                    from = pendingTransaction.from,
                    to = pendingTransaction.to,
                    value = pendingTransaction.value.toBigInteger(),
                    gasPrice = gasPrice,
                    gasLimit = 99_000L.toBigInteger())
            val estimatedGas = web3Bridge.estimateGas(actualTransaction)
            Log.d("SendTransferPresenter", "Estimated gasP = $estimatedGas")
        }
    }

    fun fetchBalance(wallet: Wallet?): Boolean {
        if (wallet == null) {
            return false
        }
        try {
            var balanceChanged = false
            var balance = BigInteger.ZERO
            when (wallet.currency) {
                Currency.TIP.name -> {
                    val processor = TipProcessor(wallet)
                    balance = processor.getBalance(wallet.address)
                }
                Currency.ETH.name -> {
                    val processor = EthProcessor()
                    balance = processor.getBalance(wallet.address)
                }
            }
            val balanceInEth =  Convert.fromWei(balance.toBigDecimal(), Convert.Unit.ETHER)
            if (balance != null) {
                if (balance != wallet.balance) {
                    wallet.balance = balance
                    wallet.lastSynced = Date()
                    walletRepository.update(wallet)
                    balanceChanged = true
                    Log.d(LOG_TAG, "Balance has changed")
                } else {
                    Log.d(LOG_TAG, "Balance still the same")
                }
                view?.onBalanceFetched(balanceInEth, Currency.valueOf(wallet.currency))
                return balanceChanged
            } else {
                view?.onWalletError()
            }
        } catch (err: Throwable) {
            view?.onWalletError()
        }

        return false
    }

    override fun loadWallets() {
        if (ethWallet == null) {
            walletRepository.findWalletForCurrency(Currency.ETH).observe(view!!, Observer { wallet ->
                if (wallet != null) {
                    ethWallet = wallet
                    Log.d(LOG_TAG, "Updating ETH wallet on change")
                    val balance = Convert.fromWei(wallet.balance.toBigDecimal(), Convert.Unit.ETHER)
                    view?.onBalanceFetched(balance, Currency.valueOf(wallet.currency))
                    fetchBalance(wallet)
                }
            })
        }

        if (tipWallet == null) {
            walletRepository.findWalletForCurrency(Currency.TIP).observe(view!!, Observer { wallet ->
                if (wallet != null && tipWallet == null) {
                    Log.d(LOG_TAG, "Updating TIP wallet on change")
                    val balance = Convert.fromWei(wallet.balance.toBigDecimal(), Convert.Unit.ETHER)
                    view?.onBalanceFetched(balance, Currency.valueOf(wallet.currency))
                    tipWallet = wallet
                    fetchBalance(wallet)
                }
            })
        }
    }

    // TODO: Use this to set the default gas price
    private fun getTransactionFee(transaction: PendingTransaction) {
        var gasDisposable:Disposable? = null
        var gasService = EthGasStationService()
        gasDisposable?.dispose()
        gasDisposable = gasService.getGasInfo().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe ({ gasInfo ->
            var gasPriceInWei = web3Bridge.getGasPrice()
            if (gasInfo != null) {
                val infoGasPrice = Convert.toWei(BigDecimal.valueOf(gasInfo.fast/10), Convert.Unit.GWEI).toBigInteger()
                gasPriceInWei = gasPriceInWei.min(infoGasPrice)
                if (gasPriceInWei == BigInteger.ZERO) {
                    gasPriceInWei = web3Bridge.getGasPrice()
                }
            }
//            this.calculateTxFee(gasPriceInWei)
        }, {
            var gasPriceInWei = web3Bridge.getGasPrice()
//            this.calculateTxFee(gasPriceInWei)
        })
    }
    override var view: SendTransfer.View? = null
}