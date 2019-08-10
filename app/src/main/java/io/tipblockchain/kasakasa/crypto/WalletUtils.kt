package io.tipblockchain.kasakasa.crypto


import android.util.Log
import java.io.File
import java.io.IOException
import java.security.InvalidAlgorithmParameterException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.web3j.crypto.*

import org.web3j.utils.Numeric

import org.web3j.crypto.Hash.sha256
import org.web3j.crypto.Keys.ADDRESS_LENGTH_IN_HEX
import org.web3j.crypto.Keys.PRIVATE_KEY_LENGTH_IN_HEX

/**
 * Utility functions for working with Wallet files.
 */
object WalletUtils {
    private const val HARDENED_BIT = -0x80000000

    private val objectMapper = ObjectMapper()

    // TODO: use SecureRandom.getInstanceStrong() or equivalent
    private val secureRandom = SecureRandom()

    val defaultKeyDirectory: String
        get() = getDefaultKeyDirectory(System.getProperty("os.name")!!)

    val testnetKeyDirectory: String
        get() = String.format(
                "%s%stestnet%skeystore", defaultKeyDirectory, File.separator, File.separator)

    val mainnetKeyDirectory: String
        get() = String.format("%s%skeystore", defaultKeyDirectory, File.separator)

    init {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    @Throws(NoSuchAlgorithmException::class, NoSuchProviderException::class, InvalidAlgorithmParameterException::class, CipherException::class, IOException::class)
    fun generateFullNewWalletFile(password: String, destinationDirectory: File): File? {

        return generateNewWalletFile(password, destinationDirectory, true)
    }

    fun add0xIfNotExists(address: String): String {
        if (!address.startsWith("0x", ignoreCase = true)) {
            return "0x$address"
        }
        return address
    }

    @Throws(NoSuchAlgorithmException::class, NoSuchProviderException::class, InvalidAlgorithmParameterException::class, CipherException::class, IOException::class)
    fun generateLightNewWalletFile(password: String, destinationDirectory: File): File? {

        return generateNewWalletFile(password, destinationDirectory, false)
    }

    @Throws(CipherException::class, IOException::class, InvalidAlgorithmParameterException::class, NoSuchAlgorithmException::class, NoSuchProviderException::class)
    fun generateNewWalletFile(
            password: String, destinationDirectory: File, useFullScrypt: Boolean): File? {

        val ecKeyPair = Keys.createEcKeyPair()
        return generateWalletFile(password, ecKeyPair, destinationDirectory, useFullScrypt)
    }


    @Throws(CipherException::class, IOException::class)
    fun getWalletFile(password: String, ecKeyPair: ECKeyPair, useFullScrypt: Boolean): WalletFile {

        val walletFile: WalletFile
        if (useFullScrypt) {
            walletFile = Wallet.createStandard(password, ecKeyPair)
        } else {
            walletFile = Wallet.createLight(password, ecKeyPair)
        }

        return walletFile
    }

    @Throws(CipherException::class, IOException::class)
    fun generateWalletFile(
            password: String, ecKeyPair: ECKeyPair, destinationDirectory: File, useFullScrypt: Boolean, saveFile: Boolean = true): File? {

        val walletFile = getWalletFile(password = password, ecKeyPair = ecKeyPair, useFullScrypt = useFullScrypt)

        if (saveFile) {
            return saveWalletFile(walletFile, destinationDirectory)
        }

        return null
    }

    fun saveWalletFile(walletFile: WalletFile, destinationDirectory: File): File {
        val fileName = getWalletFileName(walletFile)
        val destination = File(destinationDirectory, fileName)
        objectMapper.writeValue(destination, walletFile)

        return destination
    }

    /**
     * Generates a BIP-39 compatible Ethereum wallet. The private key for the wallet can
     * be calculated using following algorithm:
     * <pre>
     * Key = SHA-256(BIP_39_SEED(mnemonic, password))
    </pre> *
     *
     * @param password Will be used for both wallet encryption and passphrase for BIP-39 seed
     * @param destinationDirectory The directory containing the wallet
     * @return A BIP-39 compatible Ethereum wallet
     * @throws CipherException if the underlying cipher is not available
     * @throws IOException if the destination cannot be written to
     */
    @Throws(CipherException::class, IOException::class)
    fun generateBip39Wallet(password: String, destinationDirectory: File): Bip39Wallet? {
        val initialEntropy = ByteArray(16)
        secureRandom.nextBytes(initialEntropy)

        val mnemonic = MnemonicUtils.generateMnemonic(initialEntropy)
        val seed = MnemonicUtils.generateSeed(mnemonic, password)
        val ecKeyPair = ECKeyPair.create(sha256(seed))

        val walletFile = generateWalletFile(password, ecKeyPair, destinationDirectory, useFullScrypt = false)
        if (walletFile != null) {
            return Bip39Wallet(walletFile.name, mnemonic)
        }
        return null
    }

    @Throws(CipherException::class, IOException::class)
    fun generateBip39Mnemonic(): String {
        val initialEntropy = ByteArray(16)
        secureRandom.nextBytes(initialEntropy)

        val mnemonic = MnemonicUtils.generateMnemonic(initialEntropy)
        return mnemonic
    }

    fun getBip39WalletFileFromMnemonic(mnemonic: String, password: String, destinationDirectory: File): WalletFile {
        val seed = MnemonicUtils.generateSeed(mnemonic, password)
        val ecKeyPair = ECKeyPair.create(sha256(seed))

        return getWalletFile(password = password, ecKeyPair = ecKeyPair, useFullScrypt = false)
    }

    fun getBip44WalletFileFromMnemonic(mnemonic: String, password: String, destinationDirectory: File): WalletFile {
        val seed = MnemonicUtils.generateSeed(mnemonic, password)
        val ecKeyPair = Bip32ECKeyPair.generateKeyPair(seed)
        val path = intArrayOf(44 or HARDENED_BIT, 60 or HARDENED_BIT, 0 or HARDENED_BIT, 0, 0)
        val bip44KeyPair = Bip32ECKeyPair.deriveKeyPair(ecKeyPair, path)
        return getWalletFile(password = password, ecKeyPair = bip44KeyPair, useFullScrypt = false)
    }

    @Throws(CipherException::class, IOException::class)
    fun generateBip39WalletFromMnemonic(mnemonic: String, password: String, destinationDirectory: File, saveFile: Boolean = true): Bip39Wallet? {
        val seed = MnemonicUtils.generateSeed(mnemonic, password)
        val privateKey = ECKeyPair.create(sha256(seed))

        val walletFile = generateWalletFile(password, privateKey, destinationDirectory, useFullScrypt = false, saveFile = saveFile)
        if (walletFile != null) {
           return Bip39Wallet(walletFile.name, mnemonic)
        }
        return null
    }

    fun generateBip44WalletFromMnemonic(mnemonic: String, password: String, destinationDirectory: File, saveFile: Boolean = true): Bip39Wallet? {
        val seed = MnemonicUtils.generateSeed(mnemonic, password)
        val keyPair = Bip32ECKeyPair.generateKeyPair(seed)
        val path = intArrayOf(44 or HARDENED_BIT, 60 or HARDENED_BIT, 0 or HARDENED_BIT, 0, 0)
//        val bip44KeyPair = Bip44WalletUtils.generateBip44KeyPair(keyPair, false)
        val bip44KeyPair = Bip32ECKeyPair.deriveKeyPair(keyPair, path)

        val walletFile = generateWalletFile(password, bip44KeyPair, destinationDirectory, useFullScrypt = false, saveFile = saveFile)
        if (walletFile != null) {
            return Bip39Wallet(walletFile.name, mnemonic)
        }
        return null
    }

    @Throws(IOException::class, CipherException::class)
    fun loadCredentials(password: String, source: String): Credentials {
        return loadCredentials(password, File(source))
    }

    @Throws(IOException::class, CipherException::class)
    fun loadCredentials(password: String, source: File): Credentials {
        val walletFile = objectMapper.readValue(source, WalletFile::class.java)
        return Credentials.create(Wallet.decrypt(password, walletFile))
    }

    fun loadBip39Credentials(password: String, mnemonic: String): Credentials {
        val seed = MnemonicUtils.generateSeed(mnemonic, password)
        return Credentials.create(ECKeyPair.create(sha256(seed)))
    }

    fun getWalletFileName(walletFile: WalletFile): String {
        val dateFormat = SimpleDateFormat("'UTC--'yyyy-MM-dd'T'HH-mm-ss.SSS'--'")
        return dateFormat.format(Date()) + walletFile.address + ".json"
    }

    internal fun getDefaultKeyDirectory(osName1: String): String {
        val osName = osName1.toLowerCase()

        return if (osName.startsWith("mac")) {
            String.format(
                    "%s%sLibrary%sEthereum", System.getProperty("user.home"), File.separator,
                    File.separator)
        } else if (osName.startsWith("win")) {
            String.format("%s%sEthereum", System.getenv("APPDATA"), File.separator)
        } else {
            String.format("%s%s.ethereum", System.getProperty("user.home"), File.separator)
        }
    }

    fun isValidPrivateKey(privateKey: String): Boolean {
        val cleanPrivateKey = Numeric.cleanHexPrefix(privateKey)
        return cleanPrivateKey.length == PRIVATE_KEY_LENGTH_IN_HEX
    }

    fun isValidAddress(input: String): Boolean {
        val cleanInput = Numeric.cleanHexPrefix(input)

        try {
            Numeric.toBigIntNoPrefix(cleanInput)
        } catch (e: NumberFormatException) {
            return false
        }

        return cleanInput.length == ADDRESS_LENGTH_IN_HEX
    }
}