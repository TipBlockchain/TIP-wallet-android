package io.tipblockchain.kasakasa.data.responses

import io.tipblockchain.kasakasa.data.db.entity.Transaction

data class TransactionListResponse (var transactions: List<Transaction>)
