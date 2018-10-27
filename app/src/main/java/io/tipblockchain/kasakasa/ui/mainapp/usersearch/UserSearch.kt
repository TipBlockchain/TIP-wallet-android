package io.tipblockchain.kasakasa.ui.mainapp.usersearch

import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView

interface UserSearch {
    interface View: BaseView {
        fun onContactAdded(contact: User)
        fun onContactAddError(err: Throwable)
        fun onSearchSetupError(err: Throwable)
        fun refreshSearchList(users: List<User>)
    }

    interface Presenter: BasePresenter<View> {
        fun instantSearch(query: String)
        fun addToContacts(user: User)
    }
}
