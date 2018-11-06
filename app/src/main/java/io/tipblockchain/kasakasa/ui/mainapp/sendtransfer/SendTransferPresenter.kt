package io.tipblockchain.kasakasa.ui.mainapp.sendtransfer

import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.repository.Currency
import io.tipblockchain.kasakasa.data.db.repository.UserRepository
import java.math.BigDecimal

class SendTransferPresenter: SendTransfer.Presenter {
    private val userRepository: UserRepository = UserRepository.instance

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

    override var view: SendTransfer.View? = null
}