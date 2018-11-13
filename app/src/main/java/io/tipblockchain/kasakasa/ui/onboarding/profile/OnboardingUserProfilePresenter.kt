package io.tipblockchain.kasakasa.ui.onboarding.profile

import android.arch.lifecycle.Observer
import android.util.Log
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.data.db.repository.AuthorizationRepository
import io.tipblockchain.kasakasa.data.db.repository.UserRepository
import io.tipblockchain.kasakasa.networking.TipApiService
import java.io.File
import java.util.concurrent.TimeUnit

class OnboardingUserProfilePresenter: OnboardingUserProfile.Presenter, Observer<Wallet?> {

    override var view: OnboardingUserProfile.View? = null
    override lateinit var viewModel: OnboardingUserProfileViewModel
    override var wallet: Wallet? = null
    private var usernameDisposable: Disposable? = null
    private var createAccountDisposable: Disposable? = null
    private var uploadPhotoDisposable: Disposable? = null
    private var usernameSubject: PublishSubject<String>? = null
    private var tipApiService = TipApiService.instance
    private var userRepository = UserRepository.instance

    private val LOG_TAG = javaClass.canonicalName

    override fun attach(view: OnboardingUserProfile.View) {
        super.attach(view)
        setupUsernameSubject()
    }

    override fun detach() {
        stopObserving()
        super.detach()
    }

    override fun checkUsername(username: String) {
        usernameSubject?.onNext(username)
    }

    override fun createAccount() {
        if (wallet == null) {
            view?.onWalletNotSetupError()
            return
        }

        val user = User(id = "", name = viewModel.name, username = viewModel.username!!, address = wallet!!.address)
        createAccountDisposable = tipApiService.createUser(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe ( {newUser ->
                    if (newUser.isValid() ) {
                        UserRepository.currentUser = newUser
                        getNewAuthorization()
                    } else {
                        view?.onInvalidUser()
                    }
                }, {err ->
                    Log.d(LOG_TAG, "Error creating user: $err")
                    view?.onGenericError(err)
                }, {
                    Log.d(LOG_TAG, "complete")
                })
    }

    override fun uploadPhoto(imageFile: File) {
        uploadPhotoDisposable = userRepository.uploadProfilePhoto(imageFile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ user ->
                    if (user != null) {
                        UserRepository.currentUser = user
                        view?.onPhotoUploaded()
                    }
                }, { err ->
                    view?.onErrorUpdatingUser(err)
                })
    }

    override fun onChanged(t: Wallet?) {
        this.wallet = t
        if (t == null) {
            view?.onWalletNotSetupError()
        }
    }

    private fun getNewAuthorization() {
        AuthorizationRepository.getNewAuthorization { authorization, throwable ->
            // upload photo
            if (authorization != null) {

            }
            this.view?.onAuthorizationFetched(authorization, throwable)
        }
    }

    private fun setupUsernameSubject() {
        usernameSubject = PublishSubject.create()
        usernameDisposable = usernameSubject!!
                .debounce (1000, TimeUnit.MILLISECONDS)
                .filter(Predicate {
                    return@Predicate !it.isEmpty() && it.length >= 2
                })
                .distinctUntilChanged()
                .flatMap { tipApiService.checkUsername(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    if (it.isAvailable) {
                        view?.onUsernameAvailable()
                    } else {
                        view?.onUsernameUnavailableError()
                    }
                }, {
                    view?.onGenericError(it)
                })
    }

    private fun stopObserving() {
        createAccountDisposable?.dispose()
        usernameDisposable?.dispose()
        uploadPhotoDisposable?.dispose()

        createAccountDisposable = null
        usernameDisposable = null
        uploadPhotoDisposable = null
    }
}