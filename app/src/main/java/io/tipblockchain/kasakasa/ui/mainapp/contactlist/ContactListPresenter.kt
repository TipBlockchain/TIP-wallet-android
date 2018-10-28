package io.tipblockchain.kasakasa.ui.mainapp.contactlist

import android.arch.lifecycle.Observer
import com.android.example.github.vo.Resource
import com.android.example.github.vo.Status
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.repository.UserRepository

class ContactListPresenter: ContactList.Presenter {

    var userRepository: UserRepository = UserRepository.instance

    override fun fetchContactList() {
        userRepository.loadContacts().observe(view!!, Observer<Resource<List<User>>> {
            it.let { resource ->
                when (resource?.status) {

                    Status.SUCCESS ->{
                        view?.onContactsFetched(resource.data)
                    }
                    Status.LOADING -> {
                        view?.onContactsLoading()
                    }

                    Status.ERROR -> {
                        view?.onContactsLoadError(Error(""))
                    }
                    else -> view?.onNoContacts()
                }
            }
        })
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