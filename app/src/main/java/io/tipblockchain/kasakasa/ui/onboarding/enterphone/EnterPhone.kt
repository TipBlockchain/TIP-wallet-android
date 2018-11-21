package io.tipblockchain.kasakasa.ui.onboarding.enterphone

import io.tipblockchain.kasakasa.data.responses.PhoneVerificationRequest
import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView

interface EnterPhone {

    interface View: BaseView {
        fun onEmptyPhoneNumberError()
        fun onInvalidPhoneNumberError()
        fun onVerificationError(error: Throwable)
        fun onVerificationStarted()
    }

    interface Presenter: BasePresenter<View> {
        fun validatePhoneNumber(verificationRequest: PhoneVerificationRequest)
    }
}