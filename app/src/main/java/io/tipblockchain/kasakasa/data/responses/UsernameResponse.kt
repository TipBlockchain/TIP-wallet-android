package io.tipblockchain.kasakasa.data.responses

import com.google.gson.annotations.SerializedName

data class UsernameResponse (val message: String, @SerializedName("available") val isAvailable: Boolean) {
}