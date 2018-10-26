package io.tipblockchain.kasakasa.data.responses

import io.tipblockchain.kasakasa.data.DateSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Authorization (val token: String, @Serializable(with = DateSerializer::class) val created: Date, @Serializable(with = DateSerializer::class) val expires: Date)
