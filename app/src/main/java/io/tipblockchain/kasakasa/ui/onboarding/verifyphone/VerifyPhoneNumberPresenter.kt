package io.tipblockchain.kasakasa.ui.onboarding.verifyphone

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.app.PreferenceHelper
import io.tipblockchain.kasakasa.data.db.repository.AuthorizationRepository
import io.tipblockchain.kasakasa.data.db.repository.UserRepository
import io.tipblockchain.kasakasa.data.responses.PhoneVerificationRequest
import io.tipblockchain.kasakasa.networking.TipApiService

class VerifyPhoneNumberPresenter: VerifyPhoneNumber.Presenter {

    private var tipApiService = TipApiService.instance
    private var disposable: Disposable? = null
    private var userRepository = UserRepository.instance

    override var view: VerifyPhoneNumber.View? = null

    override fun verifyPhoneNumber(verificationRequest: PhoneVerificationRequest) {
        UserRepository.demoAccountUser = null

        disposable = tipApiService.checkPhoneVerification(verificationRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe ({ response ->
                    if (response == null) {
                        view?.onUnknownError()
                        return@subscribe
                    }

                    if (response.authorization != null && response.account != null) {
                        AuthorizationRepository.currentAuthorization = response.authorization
                        UserRepository.currentUser = response.account
                        view?.onPhoneVerifiedWithExistingAccount(response.account!!)
                    } else if (response.demoAccount != null && response.pendingSignup != null) {
                        UserRepository.demoAccountUser = response.demoAccount!!
                        PreferenceHelper.pendingSignupToken = response.pendingSignup!!.token
                        view?.onPhoneVerifiedWithPendingAndDemoAccount(response.pendingSignup!!, response.demoAccount!!)
                    } else if (response.pendingSignup != null) {
                        PreferenceHelper.pendingSignupToken = response.pendingSignup!!.token
                        view?.onPhoneVerifiedWithPendingAccount(response.pendingSignup!!)
                    } else {
                        view?.onUnknownError()
                    }
                }, { err ->
                    view?.onPhoneVerificationError(err)
                })
    }

    override fun detach() {
        disposable?.dispose()
        super.detach()
    }
}