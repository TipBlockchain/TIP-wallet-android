package io.tipblockchain.kasakasa.ui.onboarding.enterphone

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.tipblockchain.kasakasa.data.responses.PhoneVerificationRequest
import io.tipblockchain.kasakasa.networking.TipApiService

class EnterPhoneNumberPresenter: EnterPhone.Presenter {
    private val tipApiService = TipApiService.instance
    private var requestDisposable: Disposable? = null

    override var view: EnterPhone.View? = null

    override fun detach() {
        requestDisposable?.dispose()
        super.detach()
    }

    override fun validatePhoneNumber(verificationRequest: PhoneVerificationRequest) {
        if (verificationRequest.countryCode.isEmpty() || verificationRequest.phoneNumber.isEmpty()) {
            view?.onEmptyPhoneNumberError()
            return
        }

        if (verificationRequest.phoneNumber.length < 5) {
            view?.onInvalidPhoneNumberError()
            return
        }

        requestDisposable = tipApiService.startPhoneVerification(verificationRequest).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe ({  confirmation ->
            if (confirmation != null) {
                view?.onVerificationStarted()
            }
        }, { err ->
            view?.onVerificationError(err)
        })
    }

}