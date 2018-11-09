package io.tipblockchain.kasakasa.utils

import org.web3j.crypto.Hash
import java.lang.Integer.parseInt

class TextUtils {

    companion object {
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

        private fun isChecksumAddress(address: String): Boolean {
            var addressWithout0x = address.replace("0x", "")
            val addressHash = Hash.sha3String(addressWithout0x.toLowerCase())
            for (i in 0 .. addressHash.length) {
                // the nth letter should be uppercase if the nth digit of casemap is 1
                if ((parseInt(addressHash[i].toString(), 16) > 7 && addressWithout0x[i].toUpperCase() != addressWithout0x[i]) || (parseInt(addressHash[i].toString(), 16) <= 7 && addressWithout0x[i].toLowerCase() != addressWithout0x[i])) {
                    return false
                }

            }
            return false
        }
    }

}