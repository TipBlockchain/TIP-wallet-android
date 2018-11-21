package io.tipblockchain.kasakasa.crypto

import java.math.BigInteger

interface TransactionProcessor {
    fun getBalance(address: String): BigInteger?
    fun getTransactions(address: String)
    fun sendTransaction(fromAddress: String, toAddress: String, value: String, password: String)
}