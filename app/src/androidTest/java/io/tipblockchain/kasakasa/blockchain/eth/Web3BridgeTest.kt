package io.tipblockchain.kasakasa.blockchain.eth

import org.junit.Test

import org.junit.Assert.*
import org.web3j.crypto.Bip39Wallet
import java.text.DateFormat
import java.util.*

class Web3BridgeTest {

    lateinit var bip39Wallet: Bip39Wallet

    @Test
    fun loadCredentialsWithPassword() {
    }

    @Test
    fun loadBip39Credentials() {
    }

    @Test
    fun createWalletFromSeed() {
    }

    @Test
    fun createWalletFromSeed2() {
    }

    @Test
    fun createWallet() {
    }

    @Test
    fun createBIP39Wallet() {
        val wallet = Web3Bridge().createBip39Wallet("password")
        println("WalletWrapper = ${wallet.mnemonic}")
    }

    @Test
    fun unlockWallet() {
    }

    @Test
    fun sendEthTransaction() {
    }

    @Test
    fun sendTipTransaction() {
    }
}