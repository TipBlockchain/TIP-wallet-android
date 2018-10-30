package io.tipblockchain.kasakasa.crypto

import io.tipblockchain.kasakasa.blockchain.eth.Web3Bridge
import org.web3j.protocol.core.methods.response.EthGetBalance
import org.web3j.utils.Convert
import java.math.BigDecimal

class EthProcessor: TransactionProcessor {

    private val web3Bridge: Web3Bridge = Web3Bridge()

    override fun getBalance(address: String): BigDecimal {
        val ethGetBalance: EthGetBalance = web3Bridge.getEthBalanceAsync(address)
        val balanceInWei = ethGetBalance.balance
        return Convert.fromWei(balanceInWei.toBigDecimal(), Convert.Unit.ETHER)
    }

    override fun getTransactions(address: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendTransaction(fromAddress: String, toAddress: String, value: String, password: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}