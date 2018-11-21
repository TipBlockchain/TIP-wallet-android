package io.tipblockchain.kasakasa.ui.onboarding.verifyphone

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.data.responses.PhoneVerificationRequest
import io.tipblockchain.kasakasa.networking.TipApiService

class VerifyPhoneNumberPresenter: VerifyPhoneNumber.Presenter {

    private var tipApiService = TipApiService.instance
    private var disposable: Disposable? = null

    override var view: VerifyPhoneNumber.View? = null

    override fun verifyPhoneNumber(verificationRequest: PhoneVerificationRequest) {
        disposable = tipApiService.checkPhoneVerification(verificationRequest).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe ({ response ->
            if (response != null && response.authorization != null) {
                view?.onPhoneNumberVerified()
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