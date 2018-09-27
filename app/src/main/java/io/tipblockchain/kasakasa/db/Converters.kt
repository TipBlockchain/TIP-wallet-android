package io.tipblockchain.kasakasa.db

import android.arch.persistence.room.TypeConverter
import java.math.BigInteger

import java.util.Date


class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun bigIntToString(value: BigInteger): String? {
        return value.toString(10)
    }

    @TypeConverter
    fun stringToBigInt(string: String): BigInteger {
        return BigInteger(string, 10)
    }

}