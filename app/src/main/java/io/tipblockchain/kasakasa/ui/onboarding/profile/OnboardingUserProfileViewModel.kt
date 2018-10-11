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
        val user = User("", firstname!!, username!!, wallet.address)
        createAccountDisposable = TipApiService().createUser(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .onErrorReturn { User.invalid() }
                .subscribe {
                    if (it.isValid() ) {
                        User.current = it
                        Log.d("ViewModel", "user is invalid: $it")
                    } else {
                        // show error
                        Log.d("ViewModel", "user is invalid: $it")
                    }
                }
        return createAccountDisposable
    }

    fun destroy() {
        if (createAccountDisposable != null && !createAccountDisposable!!.isDisposed) {
            createAccountDisposable?.dispose()
            createAccountDisposable = null
        }
    }
}