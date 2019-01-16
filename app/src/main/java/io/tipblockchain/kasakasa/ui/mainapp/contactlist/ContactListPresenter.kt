package io.tipblockchain.kasakasa.ui.mainapp.contactlist

import android.arch.lifecycle.Observer
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.repository.UserRepository

class ContactListPresenter: ContactList.Presenter {

    private val userRepository: UserRepository = UserRepository.instance

    override fun loadContactList() {
        userRepository.loadContactsFromDb().observe(view!!, object : Observer<List<User>> {
            override fun onChanged(contacts: List<User>?) {
                if (contacts != null) {
                    view?.onContactsFetched(contacts)
                }
            }
        })
    }

    override fun fetchContactList() {
        if (view == null) {
            return
        }
        userRepository.fetchContacts()
    }

    override fun addContact(contact: User) {
        userRepository.addContact(contact) {updated, error ->
            if (updated && error == null) {
                view?.onContactAdded(contact)
            } else if (error != null) {
                view?.onContactAddError(error)
            }
        }
    }

    override fun addContacts(contacts: List<User>) {
        userRepository.addContacts(contacts)
    }

    override fun removeContact(contact: User) {
       userRepository.removeContact(contact) {updated, error ->
           if (updated && error == null) {
               view?.onContactRemoved(contact)
           } else if (error != null) {
               view?.onContactRemoveError(error)
           }
       }
    }

    override var view: ContactList.View? = null
}