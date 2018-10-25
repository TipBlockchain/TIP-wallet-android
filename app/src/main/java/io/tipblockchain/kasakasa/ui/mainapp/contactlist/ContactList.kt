package io.tipblockchain.kasakasa.ui.mainapp.contactlist

import android.arch.lifecycle.LifecycleOwner
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView

interface ContactList {
    interface View: BaseView, LifecycleOwner {
        fun onContactsFetched(contacts: List<User>?)
        fun onNoContacts()
        fun onContactsLoadError()
        fun onContactsLoading()
        fun onContactAdded(contact: User)
        fun onContactRemoved()
    }

    interface Presenter: BasePresenter<View> {
        fun fetchContactList()
        fun addContact(contact: User)
        fun addContacts(contacts: List<User>)
    }
}