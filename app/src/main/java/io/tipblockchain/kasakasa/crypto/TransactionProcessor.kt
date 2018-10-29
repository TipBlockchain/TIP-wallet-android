package io.tipblockchain.kasakasa.crypto

interface TransactionProcessor {
    fun getBalance(address: String): Unit
    fun getTransactions(address: String)
    fun sendTransaction(fromAddress: String, toAddress: String, value: String, password: String)
}