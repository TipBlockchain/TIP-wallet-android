package io.tipblockchain.kasakasa.crypto

import io.tipblockchain.kasakasa.blockchain.eth.Web3Bridge
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import org.web3j.utils.Convert
import java.math.BigDecimal

class TipProcessor: TransactionProcessor {
    var web3Bridge: Web3Bridge = Web3Bridge()

    constructor(wallet: Wallet) {
        web3Bridge = Web3Bridge(wallet)
    }
    override fun getBalance(address: String): BigDecimal {
        val balanceInWei = web3Bridge.getTipBalanceAsync(address)
        if (balanceInWei != null) {
            return Convert.fromWei(balanceInWei.toBigDecimal(), Convert.Unit.ETHER)
        }
        return BigDecimal(0)
    }

    override fun getTransactions(address: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendTransaction(fromAddress: String, toAddress: String, value: String, password: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}