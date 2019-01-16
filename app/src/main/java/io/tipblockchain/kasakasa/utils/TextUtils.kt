package io.tipblockchain.kasakasa.utils

import org.web3j.crypto.Hash
import java.lang.Integer.parseInt
import java.lang.NumberFormatException
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import org.spongycastle.jcajce.provider.digest.SHA3
import org.spongycastle.util.encoders.Hex


class TextUtils {

    companion object {

        fun isEmpty(str: String?): Boolean {
            return str == null || str.isEmpty()
        }

        fun containsSpace(str: String?): Boolean {
            return str != null && str.indexOf(" ") >= 0
        }

        fun isUsername(username: String): Boolean {
            val specialCharsRegex = "[ !@#\$%^&*()€£`~+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]/\n".toRegex()

            if (username.length < 2 || username.length > 16) {
                return false
            }
            if (specialCharsRegex.matches(username)) {
                return false
            }

            return true
        }

        fun isEthAddress(address: String): Boolean {
            val basicRegex = "^(0x)?[0-9a-f]{40}$".toRegex(setOf(RegexOption.IGNORE_CASE))
            val allLowerCase = "^(0x)?[0-9a-f]{40}$".toRegex()
            val allUpperCase = "^(0x)?[0-9A-F]{40}$".toRegex()
            if (!basicRegex.matches(address)) {
                // check if it has the basic requirements of an address
                return false
            } else if (allLowerCase.matches(address) || allUpperCase.matches(address)) {
                // If it's all small caps or all all caps, return true
                return true
            } else {
                // Otherwise check each case
                return isChecksumAddress(address)
            }

        }

        fun isNumeric(str: String): Boolean {
            return str.matches("\\d+(\\.?\\d+)?".toRegex())
        }

        private fun isChecksumAddress(address: String): Boolean {
            val addressWithout0x = address.replace("0x", "")
            val addressHash = Hash.sha3String(addressWithout0x.toLowerCase()).replace("0x", "")
            try {
                for (i in 0 until addressWithout0x.length) {
                    // the nth letter should be uppercase if the nth digit of casemap is 1
                    if ((parseInt(addressHash[i].toString(), 16) > 7 && addressWithout0x[i].toUpperCase() != addressWithout0x[i]) ||
                            (parseInt(addressHash[i].toString(), 16) <= 7 && addressWithout0x[i].toLowerCase() != addressWithout0x[i])) {
                        return false
                    }

                }
                return true
            } catch (e: Throwable) {
                Log.e("TEXTUTILS", "Caught exception: $e", e)
                return false
            }
        }

        private fun isChecksumAddress2(addr: String): Boolean {

            // First we need to check the address has the value between 0-9a-fA-F
            val regex = "^0x[0-9a-fA-F]{40}$"
            if (!addr.matches(regex.toRegex())) {
                return false
            }

            //to fetch the part after 0x
            val subAddr = addr.substring(2)
            //Make it to original lower case address
            val subAddrLower = subAddr.toLowerCase()


            // Create a SHA3256 hash (Keccak-256)
            val digestSHA3 = SHA3.Digest256()
            digestSHA3.update(subAddrLower.toByteArray())
            val digestMessage = Hex.toHexString(digestSHA3.digest())

            for (i in 0 until subAddr.length) {
                if (subAddr[i].toInt() >= 65 && subAddr[i].toInt() <= 91) {

                    println("Position Upper " + subAddr[i])
                    println("Position digest " + digestMessage.get(i))

                    val ss = Character.toString(digestMessage.get(i))
                    if (Integer.parseInt(ss, 16) <= 7) {
                        return false
                    }
                }
            }

            return true
        }
    }

}