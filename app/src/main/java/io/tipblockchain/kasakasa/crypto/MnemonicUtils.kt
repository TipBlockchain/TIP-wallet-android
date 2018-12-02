package io.tipblockchain.kasakasa.crypto

import io.tipblockchain.kasakasa.app.App
import org.spongycastle.crypto.digests.SHA512Digest
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator
import org.spongycastle.crypto.params.KeyParameter

import java.net.URL
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections

import org.web3j.crypto.Hash.sha256
import java.io.*

/**
 * Modified from org.web3j.crypto.MnemonicUtils to work better on Android with Kotlin.
 * Provides utility methods to generate random mnemonics and also generate
 * seeds from mnemonics.
 *
 * @see [Mnemonic code
 * for generating deterministic keys](https://github.com/bitcoin/bips/blob/master/bip-0039.mediawiki)
 */
object MnemonicUtils {

    private val SEED_ITERATIONS = 2048
    private val SEED_KEY_SIZE = 512
    private val WORD_LIST = populateWordList()

    /**
     * The mnemonic must encode entropy in a multiple of 32 bits. With more entropy security is
     * improved but the sentence length increases. We refer to the initial entropy length as ENT.
     * The allowed size of ENT is 128-256 bits.
     *
     * <h3>Mnemonic generation algorithm</h3>
     * Given a randomly generated initial entropy of size ENT, first a checksum is generated by
     * taking the first `ENT / 32` bits of its SHA256 hash. This checksum is appended to
     * the end of the initial entropy. Next, these concatenated bits are split into groups of
     * 11 bits, each encoding a number from 0-2047, serving as an index into a wordlist. Finally,
     * we convert these numbers into words and use the joined words as a mnemonic sentence.
     *
     * @param initialEntropy The initial entropy to generate mnemonic from
     * @return The generated mnemonic
     * @throws IllegalArgumentException If the given entropy is invalid
     */
    fun generateMnemonic(initialEntropy: ByteArray): String {
        validateInitialEntropy(initialEntropy)

        val ent = initialEntropy.size * 8
        val checksumLength = ent / 32

        val checksum = calculateChecksum(initialEntropy)
        val bits = convertToBits(initialEntropy, checksum)

        val iterations = (ent + checksumLength) / 11
        val mnemonicBuilder = StringBuilder()
        for (i in 0 until iterations) {
            val index = toInt(nextElevenBits(bits, i))
            mnemonicBuilder.append(WORD_LIST[index])

            val notLastIteration = i < iterations - 1
            if (notLastIteration) {
                mnemonicBuilder.append(" ")
            }
        }

        return mnemonicBuilder.toString()
    }

    /**
     * To create a binary seed from the mnemonic, we use the PBKDF2 function with a
     * mnemonic sentence (in UTF-8 NFKD) used as the password and the string "mnemonic"
     * + passphrase (again in UTF-8 NFKD) used as the salt. The iteration count is set
     * to 2048 and HMAC-SHA512 is used as the pseudo-random function. The length of the
     * derived key is 512 bits (= 64 bytes).
     *
     * @param mnemonic The input mnemonic which should be 128-160 bits in length containing
     * only valid words
     * @param passphrase The passphrase which will be used as part of salt for PBKDF2
     * function
     * @return Byte array representation of the generated seed
     */
    fun generateSeed(mnemonic: String, passphrase: String?): ByteArray {
        var pass = passphrase ?: ""
        validateMnemonic(mnemonic)

        val salt = String.format("mnemonic%s", pass)
        val gen = PKCS5S2ParametersGenerator(SHA512Digest())
        gen.init(mnemonic.toByteArray(Charset.forName("UTF-8")), salt.toByteArray(Charset.forName("UTF-8")), SEED_ITERATIONS)

        return (gen.generateDerivedParameters(SEED_KEY_SIZE) as KeyParameter).key
    }

    fun isValidSeedPhrase(mnemonic: String): Boolean {
        val cleanedMnemonic = mnemonic.replace("\\s+", " ")
        val wordList = cleanedMnemonic.split(" ")
        if (wordList.size != 12) {
            return false
        }
        val wordSet = wordList.toSet()
        if (wordSet.size != wordList.size) {
            return false
        }

        for (word in wordList) {
            if (!WORD_LIST.contains(word)) {
                return false
            }
        }
        return true
    }

    private fun validateMnemonic(mnemonic: String?) {
        if (mnemonic == null || mnemonic.trim { it <= ' ' }.isEmpty()) {
            throw IllegalArgumentException("Mnemonic is required to generate a seed")
        }
    }

    private fun nextElevenBits(bits: BooleanArray, i: Int): BooleanArray {
        val from = i * 11
        val to = from + 11
        return Arrays.copyOfRange(bits, from, to)
    }

    private fun validateInitialEntropy(initialEntropy: ByteArray?) {
        if (initialEntropy == null) {
            throw IllegalArgumentException("Initial entropy is required")
        }

        val ent = initialEntropy.size * 8
        if (ent < 128 || ent > 256 || ent % 32 != 0) {
            throw IllegalArgumentException("The allowed size of ENT is 128-256 bits of " + "multiples of 32")
        }
    }

    private fun convertToBits(initialEntropy: ByteArray, checksum: Byte): BooleanArray {
        val ent = initialEntropy.size * 8
        val checksumLength = ent / 32
        val totalLength = ent + checksumLength
        val bits = BooleanArray(totalLength)

        for (i in initialEntropy.indices) {
            for (j in 0..7) {
                val b = initialEntropy[i]
                bits[8 * i + j] = toBit(b, j)
            }
        }

        for (i in 0 until checksumLength) {
            bits[ent + i] = toBit(checksum, i)
        }

        return bits
    }

    private fun toBit(value: Byte, index: Int): Boolean {
        return value.toInt().ushr(7 - index) and 1 > 0
    }

    private fun toInt(bits: BooleanArray): Int {
        var value = 0
        for (i in bits.indices) {
            val isSet = bits[i]
            if (isSet) {
                value += 1 shl bits.size - i - 1
            }
        }

        return value
    }

    private fun calculateChecksum(initialEntropy: ByteArray): Byte {
        val ent = initialEntropy.size * 8
        val mask = (0xff shl 8 - ent / 32).toByte()
        val bytes = sha256(initialEntropy)

        return (bytes[0].toInt() and mask.toInt()).toByte()
    }

    private fun populateWordList(): List<String> {
        val inputStream = App.applicationContext().assets.open("wordlist-en.txt")
        try {
            return readAllLines(inputStream)
        } catch (e: Exception) {
            return emptyList()
        }

    }

    @Throws(IOException::class)
    fun readAllLines(inputStream: InputStream): List<String> {
        val br = BufferedReader(InputStreamReader(inputStream))
        val data = ArrayList<String>()
        var line: String? = br.readLine()

        while (line != null) {
            data.add(line)
            line = br.readLine()
        }

        return data
    }
}
