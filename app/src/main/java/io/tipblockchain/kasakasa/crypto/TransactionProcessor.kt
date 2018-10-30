package io.tipblockchain.kasakasa.crypto

import java.math.BigDecimal

interface TransactionProcessor {
    fun getBalance(address: String): BigDecimal
    fun getTransactions(address: String)
    fun sendTransaction(fromAddress: String, toAddress: String, value: String, password: String)
}