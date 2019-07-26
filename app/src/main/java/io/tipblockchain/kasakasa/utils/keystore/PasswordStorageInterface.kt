package io.tipblockchain.kasakasa.utils.keystore

import android.content.Context

interface PasswordStorageInterface {
    fun init(context: Context): Boolean
    // Set data which we want to keep in secret
    fun setData(key: String, data: ByteArray)

    // Get stored secret data by key
    fun getData(key: String): ByteArray?

    // Remove stored data
    fun remove(key: String)
}