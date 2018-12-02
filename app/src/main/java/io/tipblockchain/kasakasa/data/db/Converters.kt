package io.tipblockchain.kasakasa.data.db

import android.arch.persistence.room.TypeConverter
import java.math.BigInteger
import java.text.SimpleDateFormat

import java.util.Date


class Converters {

    companion object {
        val defaultDateFormat = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'"
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    fun dateToString(date: Date): String {
        return SimpleDateFormat(Converters.defaultDateFormat).format(date)
    }

    fun stringToDate(string: String): Date {
        return SimpleDateFormat(Converters.defaultDateFormat).parse(string)
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