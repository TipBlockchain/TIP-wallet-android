package io.tipblockchain.kasakasa.ui.mainapp.usersearch

import io.reactivex.subjects.PublishSubject
import io.tipblockchain.kasakasa.data.db.entity.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.data.db.repository.UserRepository
import java.util.concurrent.TimeUnit


class UserSearchPresenter: UserSearch.Presenter {

    lateinit var searchSubject: PublishSubject<String>
    private var userRepository: UserRepository = UserRepository.instance
    private var disposable: Disposable? = null

    override fun attach(view: UserSearch.View) {
        super.attach(view)
        setupSearchSubject()
    }

    override fun detach() {
        disposable?.dispose()
        super.detach()
    }

    override fun instantSearch(query: String) {
        searchSubject.onNext(query)
    }

    override fun addToContacts(user: User) {
        userRepository.addContact(user) {added, err ->
            if (added) {
                view?.onContactAdded(user)
            } else if (err != null) {
                view?.onContactAddError(err)
            }
        }
    }

    override var view: UserSearch.View? = null

    private fun setupSearchSubject() {
        searchSubject = PublishSubject.create()
        disposable = searchSubject
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter(Predicate { it: String ->
                    return@Predicate it.isNotEmpty() && it.length >= 2
                })
                .distinctUntilChanged()
                .switchMap { searchTerm ->
                    userRepository.fetchUsersBySearch(searchTerm)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                }.subscribe ({response ->
                    val users = response.users
                    view?.refreshSearchList(users)
                }, {err ->
                    view?.onSearchSetupError(err)
                })
    }
}