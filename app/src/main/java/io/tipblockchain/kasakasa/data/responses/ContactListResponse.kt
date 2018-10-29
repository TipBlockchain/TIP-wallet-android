package io.tipblockchain.kasakasa.data.responses

import com.google.gson.annotations.SerializedName
import io.tipblockchain.kasakasa.data.db.entity.User

data class ContactListResponse (var contacts: List<User> = listOf(), @SerializedName("_id") var id: String? = null){
}