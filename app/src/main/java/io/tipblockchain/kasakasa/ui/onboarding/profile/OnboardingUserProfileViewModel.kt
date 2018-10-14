package io.tipblockchain.kasakasa.ui.onboarding.profile

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.data.db.repository.UserRepository
import io.tipblockchain.kasakasa.data.db.repository.WalletRepository
import io.tipblockchain.kasakasa.networking.TipApiService

class OnboardingUserProfileViewModel: AndroidViewModel {

    var profilePhoto: BitmapDrawable? = null
    var username: String? = null
    var firstname: String? = null
    var lastname: String? = null

    var userRepository: UserRepository
    var walletRepository: WalletRepository

    var createAccountDisposable: Disposable? = null

    constructor(application: Application): super(application) {
        userRepository = UserRepository(application)
        walletRepository = WalletRepository(application)
    }

    fun canProceed(): Boolean {
        Log.d("Debug", this.toString())
        return username != null && firstname != null && lastname != null
    }

    fun signupUser() {

    }

    override fun toString(): String {
        return "{username: $username, firstname: $firstname, lastname: $lastname, photo: $profilePhoto}"
    }

    fun getPrimaryWallet(): LiveData<Wallet?> {
        return walletRepository.primaryWallet()
    }


    fun createAccount(wallet: Wallet): Disposable? {
//        val placeholderAddress = "0xd5b5e5e66765642b4843e2272ff32fb2f81a4c26"
        // TODO: switch back to users wallet
        val user = User("", firstname!!, username!!, wallet.address)
//        val user = User("", firstname!!, username!!, placeholderAddress)
        createAccountDisposable = TipApiService().createUser(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
//                .onErrorReturn { User.invalid() }
                .subscribe ( {user ->
                    Log.d("ViewModel", "User is $user")
                    if (user.isValid() ) {
                        UserRepository.currentUser = user
                        Log.d("ViewModel", "user is invalid: $user")
                    } else {
                        // show error
                        Log.d("ViewModel", "user is invalid: $user")
                    }
                }, {err ->
                    Log.d("tag", "Error: $err")
                }, {
                    Log.d("tag", "complete")
                    Log.d("tag", "complete")

                })
        return createAccountDisposable
    }

    fun destroy() {
        if (createAccountDisposable != null && !createAccountDisposable!!.isDisposed) {
            createAccountDisposable?.dispose()
            createAccountDisposable = null
        }
    }
}