package io.tipblockchain.kasakasa.data.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import java.util.*
import javax.annotation.Nonnull

@Entity(tableName = "users", indices = [Index("id"), Index("name"), Index("username")])
data class User(
        @PrimaryKey @Nonnull @ColumnInfo(name = "id") val id: String,
        @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "username") var username: String,
        @ColumnInfo(name = "address") var address: String,
        @ColumnInfo(name = "pictureUrl") var pictureUrl: String?,
        @ColumnInfo(name = "isContact") var isContact: Boolean = false,
        @ColumnInfo(name = "isBlocked") var isBlocked: Boolean = false,
        @ColumnInfo(name = "lastMessage") var lastMessage: Date? = null
    ) {
}