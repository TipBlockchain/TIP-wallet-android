package io.tipblockchain.kasakasa.blockchain.eth

import io.tipblockchain.kasakasa.blockchain.smartcontracts.TipToken
import io.tipblockchain.kasakasa.config.AppProperties
import io.tipblockchain.kasakasa.utils.FileUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.Web3jFactory
import org.web3j.protocol.http.HttpService
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.Transfer
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger
import java.io.File

import io.tipblockchain.kasakasa.crypto.*
import io.tipblockchain.kasakasa.data.db.entity.Wallet
import org.web3j.crypto.Bip39Wallet
import org.web3j.crypto.Credentials
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.Request
import org.web3j.protocol.core.methods.response.EthGasPrice
import org.web3j.protocol.core.methods.response.EthGetBalance
import org.web3j.tx.ClientTransactionManager


class Web3Bridge {

    private var web3: Web3j = Web3jFactory.build(HttpService(AppProperties.get("ether_node_url")))
    private var readOnlyTipToken: TipToken? = null
    private var unlockedTipToken: TipToken? = null

    private var gasPrice: String = "21000"
    private var gasLimit: String = "21000"


    constructor() {

    }

    constructor(wallet: Wallet) {
        loadTipSmartContract(wallet)
    }

    private fun loadTipSmartContract(wallet: Wallet) {
//        readOnlyTipToken = TipToken.load(AppProperties.get("tip_contract_address"), web3, loadCredentialsWithPassword("password", walletFile), BigInteger(gasPrice), BigInteger(gasLimit))
        readOnlyTipToken = TipToken.load(AppProperties.get("tip_contract_address"), web3, ClientTransactionManager(web3, wallet.address), BigInteger(gasPrice), BigInteger(gasLimit))
    }

    fun loadCredentialsWithPassword(password: String, file: File) : Credentials {
        val credentials = WalletUtils.loadCredentials(password, file)
        return credentials
    }

    fun createBip39Wallet(password: String): Bip39Wallet {
        val bip39Wallet = WalletUtils.generateBip39Wallet(password, FileUtils().walletsDir())
        return bip39Wallet
    }

    fun sendEthTransaction(to: String, value: String, walletFile: File, password: String): TransactionReceipt {
        val credentials = loadCredentialsWithPassword(password, walletFile)
        val receipt = Transfer.sendFunds(web3, credentials, to, BigDecimal(value), Convert.Unit.ETHER).send()
        return receipt
    }

    fun sendTipTransaction(to: String, value: BigInteger) {
        unlockedTipToken?.transfer(to, value)
    }

    fun getEthBalance(address: String): Request<*, EthGetBalance>? {
        return web3.ethGetBalance(address, DefaultBlockParameterName.LATEST)
    }

    fun getEthBalanceAsync(address: String): EthGetBalance {
        return web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get()
    }

    fun getGasPrice(): EthGasPrice {
        return web3.ethGasPrice().sendAsync().get()
    }

    fun getTipBalance(address: String) {
        readOnlyTipToken?.balanceOf(address)?.send()
    }

    fun getTipBalanceAsync(address: String): BigInteger? {
        return readOnlyTipToken?.balanceOf(address)?.sendAsync()?.get()
    }
}