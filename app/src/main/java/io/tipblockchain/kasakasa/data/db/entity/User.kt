package io.tipblockchain.kasakasa.data.db.entity

import android.arch.persistence.room.*
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.util.*
import javax.annotation.Nonnull

@Entity(tableName = "users", indices = [Index("id"), Index("name"), Index("username")])
@Serializable
data class User(
        @PrimaryKey @Nonnull @ColumnInfo(name = "id") @SerializedName("_id") val id: String,
        @ColumnInfo(name = "name") @SerializedName("fullname") var name: String,
        @ColumnInfo(name = "username") @SerializedName("username") var username: String,
        @ColumnInfo(name = "address") @SerializedName("address") var address: String,
        @ColumnInfo(name = "pictureUrl") var pictureUrl: String? = null,
        @ColumnInfo(name = "isContact") var isContact: Boolean = false,
        @ColumnInfo(name = "isBlocked") var isBlocked: Boolean = false,
        @ColumnInfo(name = "lastMessage") var lastMessage: Date? = null
    ) {

    @Ignore @SerializedName("photos") var photos: UserPhotos? = null
    set(value) {
        originalPhotoUrl = value?.original
        smallPhotoUrl = value?.small
    }

    @ColumnInfo(name = "smallPhoto")
    var smallPhotoUrl: String? = null
        get() = photos?.small ?: field

    @ColumnInfo(name = "originalPhoto")
    var originalPhotoUrl: String? = null
        get() = photos?.original ?: field

    @ColumnInfo(name = "mediumPhoto")
    var mediumPhotoUrl: String? = null
        get() = photos?.medium ?: field

    fun isValid(): Boolean {
        return !(this.username.isEmpty() || this.id.isEmpty() || this.address.isEmpty())
    }

    override fun toString(): String {
        return username
    }
}