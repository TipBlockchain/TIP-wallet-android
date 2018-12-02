package io.tipblockchain.kasakasa.data.responses

import io.tipblockchain.kasakasa.data.db.entity.Transaction

class EtherscanTxListResponse (val status: String, val message: String, val result: List<Transaction>?)
