package io.tipblockchain.kasakasa.blockchain.eth

import io.tipblockchain.kasakasa.app.AppConstants
import io.tipblockchain.kasakasa.blockchain.smartcontracts.TipToken
import io.tipblockchain.kasakasa.config.AppProperties
import io.tipblockchain.kasakasa.utils.FileUtils
import org.web3j.protocol.Web3j
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
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.tx.ClientTransactionManager
import org.web3j.tx.RawTransactionManager
import java.util.concurrent.Future


class Web3Bridge {

    private var web3: Web3j = Web3jFactory.build(AppProperties.get(AppConstants.CONFIG_ETH_NODE_URL))
    private var readOnlyTipToken: TipToken? = null

    private var defaultGasPrice = BigInteger.valueOf(21_000_000_000L)
    private var defaultGasLimit = BigInteger.valueOf(99_000L)


    constructor()

    constructor(wallet: Wallet) {
        loadTipSmartContract(wallet)
    }

    private fun loadTipSmartContract(wallet: Wallet, gasPrice: BigInteger = defaultGasPrice, gasLimit: BigInteger = defaultGasLimit) {
        readOnlyTipToken = TipToken.load(
                AppProperties.get(AppConstants.CONFIG_TIP_CONTRACT_ADDRESS),
                web3,
                ClientTransactionManager(web3, wallet.address),
                gasPrice,
                gasLimit)
    }

    private fun loadTipTokenWithCredentials(credentials: Credentials, gasPrice: BigInteger = defaultGasPrice, gasLimit: BigInteger = defaultGasLimit): TipToken {
        return TipToken.load(
                AppProperties.get(AppConstants.CONFIG_TIP_CONTRACT_ADDRESS),
                web3,
                credentials,
                gasPrice,
                gasLimit)
    }

    fun loadCredentialsForWalletWithPassword(wallet: Wallet, password: String): Credentials? {
        val walletFile = FileUtils().fileFromPath(wallet.filePath?: "")
        if (walletFile == null) {
            return null
        }
        return loadCredentialsWithPassword(password, walletFile)
    }

    fun loadCredentialsWithPassword(password: String, file: File) : Credentials {
        val credentials = WalletUtils.loadCredentials(password, file)
        return credentials
    }

    fun createBip39Wallet(password: String): Bip39Wallet? {
        val bip39Wallet = WalletUtils.generateBip39Wallet(password, FileUtils().walletsDir())
        return bip39Wallet
    }

    fun sendEthTransactionAsync(to: String, value: BigDecimal, credentials: Credentials): TransactionReceipt? {
        return Transfer.sendFunds(web3, credentials, to, value, Convert.Unit.ETHER).sendAsync().get()
    }

    fun sendEthTransactionAsyncForFuture(to: String, value: BigDecimal, gasPrice: BigInteger = defaultGasPrice, gasLimit: BigInteger = defaultGasLimit, credentials: Credentials):Future<TransactionReceipt>? {
        val transfer = Transfer(web3, RawTransactionManager(web3, credentials))
        return transfer.sendFunds(to, value, Convert.Unit.ETHER, gasPrice, gasLimit).sendAsync()
    }

    fun latestBlock(): BigInteger {
        return web3.ethBlockNumber().sendAsync().get().blockNumber
    }

    fun sendTipTransaction(to: String, value: BigDecimal, credentials: Credentials): TransactionReceipt? {
        val tipToken = loadTipTokenWithCredentials(credentials)
        val valueInWei = Convert.toWei(value, Convert.Unit.ETHER).toBigInteger()
        return tipToken.transfer(to, valueInWei)?.send()
    }

    fun sendTipTransactionAsync(to: String, value: BigDecimal, credentials: Credentials): TransactionReceipt? {
        val tipToken = loadTipTokenWithCredentials(credentials)
        val valueInWei = Convert.toWei(value, Convert.Unit.ETHER).toBigInteger()
        return tipToken.transfer(to, valueInWei)?.sendAsync()?.get()
    }

    fun sendTipTransactionAsyncForFuture(to: String, value: BigDecimal, gasPrice: BigInteger = defaultGasPrice, gasLimit: BigInteger = defaultGasLimit, credentials: Credentials): Future<TransactionReceipt>? {
        val tipToken = loadTipTokenWithCredentials(credentials, gasPrice, gasLimit)
        val valueInWei = Convert.toWei(value, Convert.Unit.ETHER).toBigInteger()
        return tipToken.transfer(to, valueInWei)?.sendAsync()
    }

    fun getEthBalance(address: String): BigInteger {
        return web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().balance
    }

    fun getTipBalance(address: String): BigInteger? {
        return readOnlyTipToken?.balanceOf(address)?.send()
    }

    fun craeteTransaction(from: String, to: String, value: BigInteger, gasPrice: BigInteger, gasLimit: BigInteger): Transaction {
        val nonce = web3.ethGetTransactionCount(to, DefaultBlockParameterName.LATEST).send().transactionCount
        val transaction = Transaction(from, nonce, gasPrice, gasLimit, to, value, "")
        return transaction
    }

    fun estimateGas(transaction: Transaction): BigInteger {
        return web3.ethEstimateGas(transaction).send().amountUsed
    }

    fun getEthBalanceAsync(address: String): BigInteger {
        return web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get().balance
    }

    fun getTipBalanceAsync(address: String): BigInteger? {
        return readOnlyTipToken?.balanceOf(address)?.sendAsync()?.get()
    }

    fun getGasPrice(): BigInteger {
        return defaultGasPrice
    }

    fun getGasLimit(): BigInteger {
        return defaultGasLimit
    }
}