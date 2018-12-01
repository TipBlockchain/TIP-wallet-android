package io.tipblockchain.kasakasa.ui.mainapp.myaccount

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.data.db.repository.UserRepository
import java.io.File

class MyAccountPresenter:MyAccount.Presenter {

    val userRepository = UserRepository.instance
    var uploadPhotoDisposable: Disposable? =  null


    override fun attach(view: MyAccount.View) {
        super.attach(view)
        userRepository.fetchMyAccount()
    }
    override fun detach() {
        uploadPhotoDisposable?.dispose()
        uploadPhotoDisposable = null
        super.detach()
    }
    override fun loadUser() {
        val user = UserRepository.currentUser
        if (user != null) {
            view?.updateUser(user)
        }
    }

    override fun uploadPhoto(imageFile: File) {
        uploadPhotoDisposable = userRepository.uploadProfilePhoto(imageFile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ user ->
                    if (user != null && user.isValid()) {
                        UserRepository.currentUser = user
                         view?.updateUser(user)
                    } else {
                        view?.onErrorUpdatingUser(Error())
                    }
                }, { err ->
                    view?.onErrorUpdatingUser(err)
                })
    }

    override var view: MyAccount.View? = null
}