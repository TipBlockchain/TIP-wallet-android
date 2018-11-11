package io.tipblockchain.kasakasa.crypto

import io.tipblockchain.kasakasa.blockchain.eth.Web3Bridge
import java.math.BigInteger

class EthProcessor: TransactionProcessor {

    private val web3Bridge: Web3Bridge = Web3Bridge()

    override fun getBalance(address: String): BigInteger? {
        val ethGetBalance = web3Bridge.getEthBalanceAsync(address)
        return ethGetBalance

    }

    override fun getTransactions(address: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendTransaction(fromAddress: String, toAddress: String, value: String, password: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}