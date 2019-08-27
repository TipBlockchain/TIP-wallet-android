package io.tipblockchain.kasakasa.data.responses

import java.io.Serializable

data class PhoneVerificationRequest (var countryCode: String, var phoneNumber: String, var verificationCode: String? = null): Serializable {

    fun getFullPhoneNumber(): String {
        return countryCode + phoneNumber
    }
}
