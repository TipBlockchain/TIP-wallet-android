package io.tipblockchain.kasakasa.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class PendingSignup (var token: String, var phone: String, var countryCode: String) {
}