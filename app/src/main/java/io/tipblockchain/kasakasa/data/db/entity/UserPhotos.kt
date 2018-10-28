package io.tipblockchain.kasakasa.data.db.entity

import kotlinx.serialization.Serializable

@Serializable
data class UserPhotos (var original: String, var medium: String, var small: String){
}