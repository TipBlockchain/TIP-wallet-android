package io.tipblockchain.kasakasa.ui.mainapp.sendtransfer

import android.arch.lifecycle.Observer
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.data.db.repository.UserRepository
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository
import io.tipblockchain.kasakasa.data.responses.PendingTransaction
import io.tipblockchain.kasakasa.utils.TextUtils
import org.web3j.utils.Convert
import java.math.BigDecimal

class SendTransferPresenter: SendTransfer.Presenter {
    private val userRepository = UserRepository.instance
    private val walletRepository = WalletRepository.instance

    override fun fetchContactList() {
        userRepository.loadContacts(view!!) { contacts, error ->
            if (contacts != null) {
                view?.onContactsFetched(contacts)
            } else if (error != null) {
                view?.onContactsFetchError(error)
            }
        }
    }

    override fun userSelected(user: User?, address: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun amountEntered(amount: BigDecimal) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun currencySelected(currency: Currency) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun nextTapped() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun validateTransfer(usernameOrAddress: String, value: String, currency: Currency, message: String) {

        walletRepository.findWalletForCurrency(currency).observe(view!!, Observer { wallet ->
            val valueAsDecimal = BigDecimal(value)
            if (wallet == null) {
                view?.onWalletError()
                return@Observer
            }
            val txValue = Convert.toWei(valueAsDecimal, Convert.Unit.ETHER).toBigInteger()
            val accountBalance = wallet.balance

            if (txValue.max(accountBalance) != accountBalance) {
                // tx value larger -> Error
                view?.onInsufficientBalanceError()
                return@Observer
            }

            var address: String?
            var username: String?

            if (TextUtils.isEthAddress(usernameOrAddress)) {
                address = usernameOrAddress
                val pending = PendingTransaction(
                        from = wallet.address,
                        fromUserId = UserRepository.currentUser!!.id,
                        to = address,
                        toUserId = null,
                        amount = value,
                        currency = currency)
                view?.onSendPendingTransaction(tx = pending)
            } else if (TextUtils.isUsername(usernameOrAddress)) {
                username = usernameOrAddress
                userRepository.findUserByUsername(username).observe(view!!, Observer { user ->
                    if (user == null) {
                        view?.onUserNotFound(username)
                    } else {
                        val pending = PendingTransaction(
                                from = wallet.address,
                                fromUserId = UserRepository.currentUser!!.id,
                                to = user.address,
                                toUserId = user.id,
                                amount = value,
                                currency = currency
                        )
                        view?.onSendPendingTransaction(tx = pending)
                    }
                })
            } else {
                view?.onInvalidRecipient()
            }

        })


    }
    override var view: SendTransfer.View? = null
}