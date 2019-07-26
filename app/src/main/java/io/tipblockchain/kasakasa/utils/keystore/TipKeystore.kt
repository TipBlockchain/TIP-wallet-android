package io.tipblockchain.kasakasa.utils.keystore

import android.content.Context
import java.nio.charset.StandardCharsets

class TipKeystore {

    companion object {

        private const val securePasswordKey = "SecurePassword"
        private const val secureSeedPhraseKey = "SecureSeedPhrase"

        private var passwordHelper: PasswordStorageHelper? = null

        fun initialize(context: Context) {
            passwordHelper = PasswordStorageHelper((context))
        }

        fun savePassword(password: String) {
            this.save(key = securePasswordKey, value = password)
        }

        fun readPassword(): String? {
            return this.read(key = securePasswordKey)
        }

        fun saveSeedPhrase(phrase: String) {
            this.save(key = secureSeedPhraseKey, value = phrase)
        }

        fun removeAllValues() {
            this.delete(securePasswordKey)
            this.delete(secureSeedPhraseKey)
        }

        fun readSeedPhrase(): String? {
            return this.read(key = secureSeedPhraseKey)
        }
        private fun save(key: String, value: String) {
            val bytes: ByteArray = value.toByteArray(StandardCharsets.UTF_8)
            passwordHelper!!.setData(key = key, data = bytes)
        }

        private fun read(key: String): String? {
            val data = passwordHelper!!.getData(key = key)
            if (data != null) {
                return String(bytes = data, charset = StandardCharsets.UTF_8)
            }
            return null
        }

        private fun delete(key: String) {
            passwordHelper!!.remove(key)
        }
    }
}