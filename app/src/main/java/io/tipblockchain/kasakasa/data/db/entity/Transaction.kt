package io.tipblockchain.kasakasa.data.db.entity

import android.arch.persistence.room.*
import com.google.gson.annotations.SerializedName
import io.tipblockchain.kasakasa.data.db.Converters
import io.tipblockchain.kasakasa.data.responses.PendingTransaction
import io.tipblockchain.kasakasa.data.serializer.BigIntegerSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigInteger

@Entity(tableName = "transactions",
        indices = [
                Index(value = ["hash"], unique = true),
            Index(value = ["from"]),
            Index(value = ["to"]),
            Index(value = ["currency"]),
            Index(value = ["from", "currency"]),
            Index(value = ["from_id"]),
            Index(value = ["to_id"])
        ])

@TypeConverters(Converters::class)
data class Transaction @JvmOverloads constructor (
        @PrimaryKey @ColumnInfo(name = "hash") var hash: String,
        @ColumnInfo(name = "blockHash") var blockHash: String,
        @ColumnInfo (name = "from") var from: String,
        @ColumnInfo (name = "to") var to: String,
        @ColumnInfo (name = "currency") var currency: String,
        @ColumnInfo (name = "value") var value: BigInteger,
        @ColumnInfo (name = "timestamp") @SerializedName("timeStamp") var time: String,
        @ColumnInfo (name = "gas") var gas: BigInteger,
        @ColumnInfo (name = "confirmations") var confirmations: BigInteger,
        @ColumnInfo (name = "nonce") var nonce: Int,
        @ColumnInfo (name = "message") var message: String?,
        @ColumnInfo (name = "txReceiptStatus") @SerializedName("txreceipt_status") var receiptStatus: String?,
        @Embedded (prefix = "from_") var fromUser: User?,
        @Embedded (prefix = "to_") var toUser: User?,
        @Ignore var fromUsername: String?,
        @Ignore var toUsername: String?) {

    constructor(): this(
            hash = "",
            blockHash = "",
            from = "",
            to = "",
            currency = "",
            value = BigInteger.ZERO,
            time = "",
            gas = BigInteger.ZERO,
            confirmations = BigInteger.ZERO,
            nonce = 0,
            message = "",
            receiptStatus = "",
            fromUser = null,
            toUser = null,
            fromUsername = "",
            toUsername = ""
    )

//    constructor(pendingTransaction: PendingTransaction, txReceipt: TransactionReceipt):
//            this(hash = txReceipt.transactionHash,
//                    blockHash = txReceipt.blockHash,
//                    from = txReceipt.from,
//                    to = txReceipt.to,
//                    currency = pendingTransaction.currency.name,
//                    value = BigInteger.ZERO,
//                    time = "",
//                    gas = txReceipt.gasUsed,
//                    confirmations = BigInteger.ZERO,
//                    nonce = 0,
//                    message = pendingTransaction.message,
//                    receiptStatus = txReceipt.status,
//                    fromUser = null,
//                    toUser = null,
//                    fromUsername = pendingTransaction.fromUsername,
//                    toUsername = pendingTransaction.toUsername
//            )

    companion object {
        fun from(pendingTransaction: PendingTransaction, txReceipt: TransactionReceipt): Transaction {
            return Transaction(
                    hash = txReceipt.transactionHash,
                    blockHash = txReceipt.blockHash,
                    from = txReceipt.from,
                    to = txReceipt.to,
                    currency = pendingTransaction.currency.name,
                    value = BigInteger.ZERO,
                    time = "",
                    gas = txReceipt.gasUsed,
                    confirmations = BigInteger.ZERO,
                    nonce = 0,
                    message = pendingTransaction.message,
                    receiptStatus = txReceipt.status,
                    fromUser = null,
                    toUser = null,
                    fromUsername = pendingTransaction.fromUsername,
                    toUsername = pendingTransaction.toUsername
            )
        }
    }

}

