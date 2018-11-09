package io.tipblockchain.kasakasa.data.responses

import io.tipblockchain.kasakasa.data.db.repository.Currency
import java.io.Serializable

data class PendingTransaction (var from: String, var fromUserId: String, var to: String, var toUserId: String?, var amount: String, var currency: Currency): Serializable
