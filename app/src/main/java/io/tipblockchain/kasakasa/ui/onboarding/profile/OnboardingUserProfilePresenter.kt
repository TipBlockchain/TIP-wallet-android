package io.tipblockchain.kasakasa.ui.onboarding.profile

import android.util.Log
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.tipblockchain.kasakasa.data.db.entity.User
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import io.tipblockchain.kasakasa.data.db.repository.UserRepository
import io.tipblockchain.kasakasa.networking.TipApiService
import java.util.concurrent.TimeUnit

class OnboardingUserProfilePresenter: OnboardingUserProfile.Presenter {

    override var view: OnboardingUserProfile.View? = null
    override lateinit var viewModel: OnboardingUserProfileViewModel
    override var wallet: Wallet? = null
    private var checkUsernameDisposable: Disposable? = null
    private var usernameDisposable: Disposable? = null
    private var createAccountDisposable: Disposable? = null
    private var usernameSubject: PublishSubject<String>? = null

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
        this.checkUsernameDelayed(username)
    }

    override fun createAccount() {
        if (wallet == null) {
            view?.onWalletNotSetupError()
            return
        }

        val user = User(id = "", name = viewModel.name, username = viewModel.username!!, address = wallet!!.address)
        createAccountDisposable = TipApiService().createUser(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe ( {newUser ->
                    if (newUser.isValid() ) {
                        UserRepository.currentUser = newUser
                        view?.onAccountCreated()
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

    private fun setupUsernameSubject() {
        usernameSubject = PublishSubject.create()
        checkUsernameDisposable = usernameSubject!!.flatMap { TipApiService().checkUsername(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    if (!it.isAvailable) {
                        view?.onUsernameUnavailableError()
                    }
                },
                {
                    view?.onGenericError(it)
                })
    }

    private fun checkUsernameDelayed(username: String) {
        usernameDisposable?.dispose()
        // Wait 1.5 seconds after user types before making a request, so we don't make unnecessary requests
        usernameDisposable = Completable.timer(1500, TimeUnit.MILLISECONDS, Schedulers.io())
                .subscribe{
                    usernameSubject?.onNext(username)
                }
    }

    fun stopObserving() {
        createAccountDisposable?.dispose()
        checkUsernameDisposable?.dispose()
        usernameDisposable?.dispose()

        createAccountDisposable = null
        checkUsernameDisposable = null
        usernameDisposable = null
    }
}