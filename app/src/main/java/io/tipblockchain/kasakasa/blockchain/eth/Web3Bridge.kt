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
import org.web3j.crypto.*
import java.io.File
import java.util.*


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

    fun loadBip39Credentials(seed: String, password: String) : Credentials {
        val credentials = WalletUtils.loadBip39Credentials(seed, password)
        return credentials
    }

    fun createWalletFromSeed(seed: String): JSONObject {

        val processJson = JSONObject()

        try {
            val ecKeyPair = Keys.createEcKeyPair()
            val privateKeyInDec = ecKeyPair.privateKey

            val sPrivatekeyInHex = privateKeyInDec.toString(16)

            val aWallet = Wallet.createLight(seed, ecKeyPair)
            val sAddress = aWallet.getAddress()


            processJson.put("address", "0x$sAddress")
            processJson.put("privatekey", sPrivatekeyInHex)


        } catch (e: CipherException) {
            //
        } catch (e: InvalidAlgorithmParameterException) {
            //
        } catch (e: NoSuchAlgorithmException) {
            //
        } catch (e: NoSuchProviderException) {
            //
        }

        return processJson
    }

    fun createWalletFromSeed2(seedCode: String, password: String) {
        val seed = DeterministicSeed(seedCode, null, password, Date().time)
        val chain = DeterministicKeyChain.builder().seed(seed).build()
        val keyPath = HDUtils.parsePath("M/44H/60H/0H/0/0")
        val key = chain.getKeyByPath(keyPath, true)
        val privKey = key.privKey
        val privateKeyString = privKey.toString(16)

// Web3j
        val credentials = Credentials.create(privateKeyString)
        println(credentials.address)
        val json = JSONObject()
        json.put("address", "0x${credentials.address}")
        json.put("privateKey", privateKeyString)
    }


    fun createNewLightWallet(password: String): String {
        return WalletUtils.generateLightNewWalletFile(password, FileUtils().walletsDir())
    }

    fun createNewFullWallet(password: String) : String {
        return WalletUtils.generateFullNewWalletFile(password, FileUtils().walletsDir())
    }

    fun createBip39Wallet(password: String): Bip39Wallet {
        val bip39Wallet = WalletUtils.generateBip39Wallet(password, FileUtils().walletsDir())
        return bip39Wallet
    }

    fun sendEthTransaction(to: String, value: String, walletFile: File): TransactionReceipt {
        val credentials = loadCredentialsWithPassword("password", walletFile)
        val receipt = Transfer.sendFunds(web3, credentials, to, BigDecimal(value), Convert.Unit.ETHER).send()
        return receipt
    }

    fun sendTipTransaction(to: String, value: BigInteger) {
        tipToken!!.transfer(to, value)
    }
}