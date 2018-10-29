package io.tipblockchain.kasakasa.blockchain.eth

import android.content.Context
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
import org.web3j.crypto.Wallet.createLight
import org.json.JSONObject
import java.security.InvalidAlgorithmParameterException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import org.bitcoinj.crypto.HDUtils
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.wallet.DeterministicKeyChain
import org.bitcoinj.wallet.DeterministicSeed
import java.io.File
import java.security.SecureRandom
import java.util.*

import io.tipblockchain.kasakasa.crypto.*
import org.web3j.crypto.Keys
import org.web3j.crypto.Bip39Wallet
import org.web3j.crypto.Wallet
import org.web3j.crypto.Credentials
import org.web3j.crypto.CipherException
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.Request
import org.web3j.protocol.core.methods.response.EthGasPrice
import org.web3j.protocol.core.methods.response.EthGetBalance


class Web3Bridge {

    init {
//        loadTipSmartContract()

    }

    private var web3: Web3j = Web3jFactory.build(HttpService("https://rinkeby.infura.io/SSWOxqisHlJoSVWYy09p "))
    private var tipToken: TipToken? = null

    private var gasPrice: String = "21000"
    private var gasLimit: String = "21000"


    private fun loadTipSmartContract(walletFile: File) {
        tipToken = TipToken.load(AppProperties.get("tip_contract_address"), web3, loadCredentialsWithPassword("password", walletFile), BigInteger(gasPrice), BigInteger(gasLimit))
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
        tipToken!!.transfer(to, value)
    }

    fun getBalance(address: String): Request<*, EthGetBalance>? {
        return web3.ethGetBalance(address, DefaultBlockParameterName.LATEST)
    }

    fun getEthBalanceAsync(address: String): EthGetBalance {
        return web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get()
    }

    fun getGasPrice(): EthGasPrice {
        return web3.ethGasPrice().sendAsync().get()
    }

    fun getTipBalance(address: String) {
        tipToken!!.balanceOf(address).send()
    }
}