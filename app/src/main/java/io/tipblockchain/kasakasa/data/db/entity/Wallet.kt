package io.tipblockchain.kasakasa.data.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import java.io.File
import java.math.BigInteger
import java.util.*

@Entity(tableName = "wallets", primaryKeys = [ "address", "currency"], indices = [Index("currency"), Index(value = "address")])
data class Wallet (
        @ColumnInfo(name = "address") var address: String,
        @ColumnInfo(name = "filePath") val filePath: String?,
        @ColumnInfo(name = "created") val created: Date = Date(),
        @ColumnInfo(name = "balance") val balance: BigInteger = BigInteger.ZERO,
        @ColumnInfo(name = "currency") val currency: String = "TIP",
        @ColumnInfo(name = "isPrimary") val isPrimary: Boolean = true,
        @ColumnInfo(name = "lastSynced") val lastSynced: Date = Date()
        )
