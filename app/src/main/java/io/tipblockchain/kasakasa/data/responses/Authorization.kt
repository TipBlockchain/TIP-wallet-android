package io.tipblockchain.kasakasa.data.responses

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Authorization (val token: String, val created: Date, val expires: Date) {
}