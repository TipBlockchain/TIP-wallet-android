package io.tipblockchain.kasakasa.data.responses

import io.tipblockchain.kasakasa.data.db.repository.Currency
import java.io.Serializable
import java.math.BigDecimal

data class PendingTransaction (var from: String, var fromUsername: String, var to: String, var toUsername: String?, var value: BigDecimal, var currency: Currency, var message: String? = null): Serializable
