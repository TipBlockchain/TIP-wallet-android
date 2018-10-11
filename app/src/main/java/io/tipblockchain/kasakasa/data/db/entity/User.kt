package io.tipblockchain.kasakasa.data.db.entity

import android.arch.persistence.room.*
import io.tipblockchain.kasakasa.app.Preferences
import kotlinx.serialization.json.JSON
import java.util.*
import javax.annotation.Nonnull

@Entity(tableName = "users", indices = [Index("id"), Index("name"), Index("username")])
data class User(
        @PrimaryKey @Nonnull @ColumnInfo(name = "id") val id: String,
        @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "username") var username: String,
        @ColumnInfo(name = "address") var address: String,
        @ColumnInfo(name = "pictureUrl") var pictureUrl: String? = null,
        @ColumnInfo(name = "isContact") var isContact: Boolean = false,
        @ColumnInfo(name = "isBlocked") var isBlocked: Boolean = false,
        @ColumnInfo(name = "lastMessage") var lastMessage: Date? = null
    ) {

    @Ignore
    var isValid: () -> Boolean = {
        !(this.username.isEmpty() || this.id.isEmpty() || this.address.isEmpty())
    }

    companion object {
        var current: User? = null
            set(_) { save() }

        fun invalid() : User =  User("", "", "", "")

        fun fetch(): User? {
            if (current == null) {
                val userJson = Preferences.currentUser
                if (userJson != null) {
                    synchronized(User::class.java) {
                        if (current == null) {
                            current = JSON.parse(userJson)
                        }
                    }
                }
            }
            return current
        }

        fun save() {
            val userJson = JSON.stringify(this)
            Preferences.currentUser = userJson
        }
    }
}