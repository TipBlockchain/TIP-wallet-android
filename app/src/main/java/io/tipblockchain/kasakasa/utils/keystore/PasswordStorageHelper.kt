package io.tipblockchain.kasakasa.utils.keystore


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.security.KeyChain
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import io.tipblockchain.kasakasa.R
import io.tipblockchain.kasakasa.app.PreferenceHelper

import java.io.IOException
import java.math.BigInteger
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.PrivateKey
import java.security.PublicKey
import java.security.UnrecoverableEntryException
import java.security.UnrecoverableKeyException
import java.security.cert.CertificateException
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.InvalidKeySpecException
import java.util.Calendar
import java.util.GregorianCalendar

import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.security.auth.x500.X500Principal


class PasswordStorageHelper(context: Context) {

    private var passwordStorage: PasswordStorageImpl? = null

    init {
        passwordStorage = PasswordStorageHelper_SDK18()

        var isInitialized = false

        try {
            isInitialized = passwordStorage!!.init(context)
        } catch (ex: Exception) {
            Log.e(LOG_TAG, "PasswordStorage initialisation error:" + ex.message, ex)
        }
    }

    fun setData(key: String, data: ByteArray) {
        passwordStorage!!.setData(key, data)
    }

    fun getData(key: String): ByteArray? {
        return passwordStorage!!.getData(key)
    }

    fun remove(key: String) {
        passwordStorage!!.remove(key)
    }

    private interface PasswordStorageImpl {
        fun init(context: Context): Boolean

        fun setData(key: String, data: ByteArray)

        fun getData(key: String): ByteArray?

        fun remove(key: String)
    }

    private class PasswordStorageHelper_SDK18 : PasswordStorageImpl {

        private var preferences: SharedPreferences? = null
        private var alias: String? = null

        @SuppressLint("NewApi", "TrulyRandom")
        override fun init(context: Context): Boolean {
            preferences = context.getSharedPreferences(PreferenceHelper.getPreferenceFilename(), Context.MODE_PRIVATE)
            alias = context.getString(R.string.app_package)

            val ks: KeyStore?

            try {
                ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE)

                //Use null to load Keystore with default parameters.
                ks!!.load(null)

                // Check if Private and Public already keys exists. If so we don't need to generate them again
                val privateKey = ks.getKey(alias, null) as? PrivateKey
                if (privateKey != null && ks.getCertificate(alias) != null) {
                    val publicKey = ks.getCertificate(alias).publicKey
                    if (publicKey != null) {
                        // All keys are available.
                        return true
                    }
                }
            } catch (ex: Exception) {
                return false
            }

            // Create a start and end time, for the validity range of the key pair that's about to be
            // generated.
            val start = GregorianCalendar()
            val end = GregorianCalendar()
            end.add(Calendar.YEAR, 10)

            // Specify the parameters object which will be passed to KeyPairGenerator
            val spec: AlgorithmParameterSpec
            if (android.os.Build.VERSION.SDK_INT < 23) {
                spec = android.security.KeyPairGeneratorSpec.Builder(context)
                        // Alias - is a key for your KeyPair, to obtain it from Keystore in future.
                        .setAlias(alias!!)
                        // The subject used for the self-signed certificate of the generated pair
                        .setSubject(X500Principal("CN=" + alias!!))
                        // The serial number used for the self-signed certificate of the generated pair.
                        .setSerialNumber(BigInteger.valueOf(1337))
                        // Date range of validity for the generated pair.
                        .setStartDate(start.time).setEndDate(end.time)
                        .build()
            } else {
                spec = KeyGenParameterSpec.Builder(alias!!, KeyProperties.PURPOSE_DECRYPT)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        .build()
            }

            // Initialize a KeyPair generator using the the intended algorithm (in this example, RSA
            // and the KeyStore. This example uses the AndroidKeyStore.
            val kpGenerator: KeyPairGenerator
            try {
                kpGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA, KEYSTORE_PROVIDER_ANDROID_KEYSTORE)
                kpGenerator.initialize(spec)
                // Generate private/public keys
                kpGenerator.generateKeyPair()
            } catch (e: NoSuchAlgorithmException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                    // Just ignore any errors here
                }

            } catch (e: InvalidAlgorithmParameterException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            } catch (e: NoSuchProviderException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            }

            // Check if device support Hardware-backed keystore
            try {
                val isHardwareBackedKeystoreSupported: Boolean
                if (android.os.Build.VERSION.SDK_INT < 23) {
                    isHardwareBackedKeystoreSupported = KeyChain.isBoundKeyAlgorithm(KeyProperties.KEY_ALGORITHM_RSA)
                } else {
                    val privateKey = ks.getKey(alias, null) as PrivateKey
                    KeyChain.isBoundKeyAlgorithm(KeyProperties.KEY_ALGORITHM_RSA)
                    val keyFactory = KeyFactory.getInstance(privateKey.algorithm, "AndroidKeyStore")
                    val keyInfo = keyFactory.getKeySpec(privateKey, KeyInfo::class.java)
                    isHardwareBackedKeystoreSupported = keyInfo.isInsideSecureHardware
                }
                Log.d(LOG_TAG, "Hardware-Backed Keystore Supported: $isHardwareBackedKeystoreSupported")
            } catch (e: KeyStoreException) {
            } catch (e: NoSuchAlgorithmException) {
            } catch (e: UnrecoverableKeyException) {
            } catch (e: InvalidKeySpecException) {
            } catch (e: NoSuchProviderException) {
            }

            return true
        }

        override fun setData(key: String, data: ByteArray) {
            var ks: KeyStore? = null
            try {
                ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE)

                ks!!.load(null)
                if (ks.getCertificate(alias) == null) return

                val publicKey = ks.getCertificate(alias).publicKey

                if (publicKey == null) {
                    Log.d(LOG_TAG, "Error: Public key was not found in Keystore")
                    return
                }

                val value = encrypt(publicKey, data)

                val editor = preferences!!.edit()
                editor.putString(key, value)
                editor.commit()
            } catch (e: NoSuchAlgorithmException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                    // Just ignore any errors here
                }

            } catch (e: InvalidKeyException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            } catch (e: NoSuchPaddingException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            } catch (e: IllegalBlockSizeException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            } catch (e: BadPaddingException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            } catch (e: NoSuchProviderException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            } catch (e: InvalidKeySpecException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            } catch (e: KeyStoreException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            } catch (e: CertificateException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            } catch (e: IOException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            }

        }

        override fun getData(key: String): ByteArray? {
            var ks: KeyStore? = null
            try {
                ks = KeyStore.getInstance(KEYSTORE_PROVIDER_ANDROID_KEYSTORE)
                ks!!.load(null)
                val privateKey = ks.getKey(alias, null) as? PrivateKey
                if (privateKey != null) {
                    return decrypt(privateKey, preferences!!.getString(key, null))
                }
            } catch (e: KeyStoreException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                    // Just ignore any errors here
                }

            } catch (e: NoSuchAlgorithmException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            } catch (e: CertificateException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            } catch (e: IOException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            } catch (e: UnrecoverableEntryException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            } catch (e: InvalidKeyException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            } catch (e: NoSuchPaddingException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            } catch (e: IllegalBlockSizeException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            } catch (e: BadPaddingException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            } catch (e: NoSuchProviderException) {
                try {
                    ks?.deleteEntry(alias)
                } catch (e1: Exception) {
                }

            }

            return null
        }

        override fun remove(key: String) {
            val editor = preferences!!.edit()
            editor.remove(key)
            editor.commit()
        }

        companion object {

            private val KEY_ALGORITHM_RSA = "RSA"

            private val KEYSTORE_PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore"
            private val RSA_ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding"

            @SuppressLint("TrulyRandom")
            @Throws(NoSuchAlgorithmException::class, NoSuchPaddingException::class, InvalidKeyException::class, IllegalBlockSizeException::class, BadPaddingException::class, NoSuchProviderException::class, InvalidKeySpecException::class)
            private fun encrypt(encryptionKey: PublicKey, data: ByteArray): String {

                val cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING)
                cipher.init(Cipher.ENCRYPT_MODE, encryptionKey)
                val encrypted = cipher.doFinal(data)
                return Base64.encodeToString(encrypted, Base64.DEFAULT)
            }

            @Throws(NoSuchAlgorithmException::class, NoSuchPaddingException::class, InvalidKeyException::class, IllegalBlockSizeException::class, BadPaddingException::class, NoSuchProviderException::class)
            private fun decrypt(decryptionKey: PrivateKey, encryptedData: String?): ByteArray? {
                if (encryptedData == null)
                    return null
                val encryptedBuffer = Base64.decode(encryptedData, Base64.DEFAULT)
                val cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING)
                cipher.init(Cipher.DECRYPT_MODE, decryptionKey)
                return cipher.doFinal(encryptedBuffer)
            }
        }
    }

    companion object {

        private val LOG_TAG = PasswordStorageHelper::class.java.simpleName
    }
}