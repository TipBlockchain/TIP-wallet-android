package io.tipblockchain.kasakasa.ui.mainapp.myaccount

import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView
import java.io.File

interface MyAccount {
    interface View: BaseView {
        fun updateUser(user: User)
        fun onAboutMeUpdated(aboutMe: String)
        fun onFullnameUpdated(fullname: String)
        fun onErrorUpdatingUser(error: Throwable)
    }

    interface Presenter: BasePresenter<View> {
        fun loadUser()
        fun uploadPhoto(imageFile: File)
        fun saveFullname(fullname: String)
        fun saveAboutMe(aboutMe: String)
        fun saveUser()
    }
}