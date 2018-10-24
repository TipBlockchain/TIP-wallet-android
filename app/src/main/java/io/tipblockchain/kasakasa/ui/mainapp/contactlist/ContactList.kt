package io.tipblockchain.kasakasa.ui.mainapp.contactlist

import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView

interface ContactList {
    interface View: BaseView {
        fun onContactsFetched()
        fun onNoContacts()
        fun onContactAdded()
        fun onContactRemoved()
    }

    interface Presenter: BasePresenter<View> {
        fun fetchContactList()
        fun addContacts(contacts: List<User>)
    }
}