package io.tipblockchain.kasakasa.data.responses

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class PhoneVerificationConfirmation (val message: String, @SerializedName("seconds_to_expire") val secondsToExpire: Int, val uuid: String, val success: Boolean)
