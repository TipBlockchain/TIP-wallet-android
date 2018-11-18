package io.tipblockchain.kasakasa.data.db.entity

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.ForeignKey.SET_NULL
import com.google.gson.annotations.SerializedName
import java.math.BigInteger

@Entity(tableName = "transactions",
        indices = [
                Index(value = ["hash"], unique = true),
            Index(value = ["from"]),
            Index(value = ["to"]),
            Index(value = ["currency"]),
            Index(value = ["from", "currency"]),
            Index(value = ["toTipUserId"]),
            Index(value = ["fromTipUserId"])
        ],
        foreignKeys = [
                ForeignKey(
                        entity = User::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("fromTipUserId"),
                        onDelete = SET_NULL),
                ForeignKey(
                        entity = User::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("toTipUserId"),
                        onDelete = SET_NULL)
        ])

class Transaction(
        @PrimaryKey @ColumnInfo(name = "hash") val hash: String,
        @ColumnInfo(name = "blockHash") val blockHash: String,
        @ColumnInfo (name = "from") val from: String,
        @ColumnInfo (name = "to") val to: String,
        @ColumnInfo (name = "fromTipUserId") var fromTipUserId: String?,
        @ColumnInfo (name = "toTipUserId") var toTipUserId: String?,
        @ColumnInfo (name = "currency") var currency: String,
        @ColumnInfo (name = "value") val value: BigInteger,
        @ColumnInfo (name = "timestamp") @SerializedName("timeStamp") val time: String,
        @ColumnInfo (name = "gas") val gas: BigInteger,
        @ColumnInfo (name = "gasPrice") val gasPrice: BigInteger,
        @ColumnInfo (name = "confirmations") var confirmations: BigInteger,
        @ColumnInfo (name = "nonce") val nonce: Int,
        @ColumnInfo (name = "message") var message: String?,
        @ColumnInfo (name = "tokenSymbol") var tokenSymbol: String?,
        @ColumnInfo (name = "txReceiptStatus") @SerializedName("txreceipt_status") var receiptStatus: String?) {
}

