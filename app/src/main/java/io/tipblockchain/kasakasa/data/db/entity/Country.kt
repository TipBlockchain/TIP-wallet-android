package io.tipblockchain.kasakasa.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import javax.annotation.Nonnull

@Entity(tableName = "countries", indices = [])
data class Country (
        @PrimaryKey @Nonnull @ColumnInfo(name = "id") @SerializedName("_id") val id: String,
        @ColumnInfo(name = "isRestricted") val isRestricted: Boolean,
        @ColumnInfo(name = "isBlocked") val isBlocked: Boolean,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "niceName") val niceName: String,
        @ColumnInfo(name = "countryCode") val countryCode: Int,
        @ColumnInfo(name = "iso") val iso: String,
        @ColumnInfo(name = "iso3") val iso3: String,
        @ColumnInfo(name = "numericCode") val numericCode: Int
){
}