package io.tipblockchain.kasakasa.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import java.math.BigInteger
import java.util.*

@Entity(tableName = "wallets", indices = [Index("currency")])
data class Wallet (
        @PrimaryKey @NonNull @ColumnInfo(name = "address") var address: String,
        @ColumnInfo(name = "filePath") val filePath: String,
        @ColumnInfo(name = "created") val created: Date,
        @ColumnInfo(name = "balance") val balance: BigInteger,
        @ColumnInfo(name = "currency") val currency: String,
        @ColumnInfo(name = "isPrimary") val isPrimary: Boolean,
        @ColumnInfo(name = "lastSynced") val lastSynced: Date
        ){
}