package io.tipblockchain.kasakasa.blockchain.wallet

import io.tipblockchain.kasakasa.blockchain.eth.Web3Bridge
import io.tipblockchain.kasakasa.utils.FileUtils
import org.web3j.crypto.Wallet


object WalletManager {

    private var wallets: MutableList<WalletWrapper> = mutableListOf()

    fun createBIP39Wallet(password: String): WalletWrapper? {
        val web3Bridge = Web3Bridge()
        val bip39Wallet = web3Bridge.createBip39Wallet(password)
        val walletFile = FileUtils().fileForWalletFilename(bip39Wallet.filename)
        if (walletFile != null && walletFile.exists()) {
            val credentials = web3Bridge.loadCredentialsWithPassword(password, walletFile)
            val walletWrapper = WalletWrapper(credentials.address, walletFile)
            wallets.add(walletWrapper)
            return walletWrapper
        }

        return null
    }

    fun defaultWalletWrapper(): WalletWrapper? {
        return if (wallets != null && wallets.count() > 0) wallets.first() else null
    }
}