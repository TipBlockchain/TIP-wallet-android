package io.tipblockchain.kasakasa.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import java.math.BigInteger
import java.util.*
import javax.annotation.Nonnull

@Entity(tableName = "transactions", indices = [Index(value = ["hash"], unique = true), Index(value = ["from"]), Index(value = ["to"]), Index(value = ["currency"]), Index(value = ["from", "currency"])])
class Transaction(
        @PrimaryKey @Nonnull @ColumnInfo(name = "id") val id: String,
        @ColumnInfo(name = "hash") val hash: String,
        @ColumnInfo (name = "from") val from: User,
        @ColumnInfo (name = "to") val to: User,
        @ColumnInfo (name = "currency") val currency: String,
        @ColumnInfo (name = "value") val value: BigInteger,
        @ColumnInfo (name = "time") val time: Date,
        @ColumnInfo (name = "message") val message: String) {

}