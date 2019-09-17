package io.tipblockchain.kasakasa.data.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import java.io.File
import java.io.Serializable
import java.math.BigInteger
import java.util.*

@Entity(tableName = "wallets", primaryKeys = [ "address", "currency"], indices = [Index("currency"), Index("address")])
data class Wallet (
        @ColumnInfo(name = "address") var address: String,
        @ColumnInfo(name = "filePath") var filePath: String?,
        @ColumnInfo(name = "created") val created: Date = Date(),
        @ColumnInfo(name = "balance") var balance: BigInteger = BigInteger.ZERO,
        @ColumnInfo(name = "currency") val currency: String = "TIP",
        @ColumnInfo(name = "isPrimary") var isPrimary: Boolean = true,
        @ColumnInfo(name = "blockNumber") var blockNumber: BigInteger = BigInteger.ZERO,
        @ColumnInfo(name = "startBlockNumber") var startBlockNumber: BigInteger = BigInteger.ZERO,
        @ColumnInfo(name = "name") var name: String? = null,
        @ColumnInfo(name = "lastSynced") var lastSynced: Date = Date()
        ): Serializable {

        fun displayName(): String {
               return if (name != null)  name!! else currency
        }
}
