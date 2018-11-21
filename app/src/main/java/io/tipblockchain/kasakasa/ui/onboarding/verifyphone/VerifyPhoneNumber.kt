package io.tipblockchain.kasakasa.ui.onboarding.verifyphone

import io.tipblockchain.kasakasa.data.responses.PhoneVerificationRequest
import io.tipblockchain.kasakasa.ui.BasePresenter
import io.tipblockchain.kasakasa.ui.BaseView

interface VerifyPhoneNumber {

    interface View: BaseView {
        fun onPhoneNumberVerified()
        fun onUnknownError()
        fun onPhoneVerificationError(error: Throwable)
    }

    interface Presenter: BasePresenter<View> {
        fun verifyPhoneNumber(verificationRequest: PhoneVerificationRequest)
    }
}