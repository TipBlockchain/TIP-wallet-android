package io.tipblockchain.kasakasa.ui.mainapp.contactlist

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import com.android.example.github.vo.Resource
import com.android.example.github.vo.Status
import io.tipblockchain.kasakasa.app.App
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.repository.UserRepository

class ContactListPresenter: ContactList.Presenter {
    var userRepository: UserRepository

    constructor() {
        userRepository = UserRepository(App.application())
    }

    override fun fetchContactList() {
        userRepository.loadContacts().observe(view!!, Observer<Resource<List<User>>> {
            it.let { resource ->
                when (resource?.status) {

                    Status.SUCCESS ->{
                        view!!.onContactsFetched(resource.data)
                    }
                    Status.LOADING -> {
                        view!!.onContactsLoading()
                    }

                    Status.ERROR -> {
                        view!!.onContactsLoadError()
                    }
                }
            }
        })
    }

    override fun addContact(contact: User) {
        userRepository.addContact(contact) {updated, error ->
            if (updated && error == null) {
                view!!.onContactAdded(contact)
            }
        }
    }
    override fun addContacts(contacts: List<User>) {
        userRepository.addContacts(contacts)
    }

    override var view: ContactList.View? = null
}