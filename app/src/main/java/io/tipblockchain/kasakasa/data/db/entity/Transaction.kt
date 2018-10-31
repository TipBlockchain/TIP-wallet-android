package io.tipblockchain.kasakasa.data.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import org.web3j.protocol.core.methods.response.EthGetUncleCountByBlockHash
import java.math.BigInteger
import java.util.*
import javax.annotation.Nonnull

@Entity(tableName = "transactions", indices = [Index(value = ["hash"], unique = true), Index(value = ["from"]), Index(value = ["to"]), Index(value = ["currency"]), Index(value = ["from", "currency"])])
class Transaction(
        @PrimaryKey @Nonnull @ColumnInfo(name = "id") val id: String,
        @ColumnInfo(name = "hash") val hash: String,
        @ColumnInfo(name = "blockhash") val blockHash: String,
        @ColumnInfo (name = "from") val from: String,
        @ColumnInfo (name = "to") val to: String,
        @ColumnInfo (name = "tipFromUser") val tipFromUser: String?,
        @ColumnInfo (name = "tipToUser") val tipToUser: String?,
        @ColumnInfo (name = "currency") val currency: String,
        @ColumnInfo (name = "value") val value: BigInteger,
        @ColumnInfo (name = "timestamp") val time: Date,
        @ColumnInfo (name = "status") val status: String,
        @ColumnInfo (name = "gas") val gas: BigInteger,
        @ColumnInfo (name = "gasPrice") val gasPrice: BigInteger,
        @ColumnInfo (name = "confirmations") val confirmations: BigInteger,
        @ColumnInfo (name = "nonce") val nonce: Int,
        @ColumnInfo (name = "message") val message: String)
