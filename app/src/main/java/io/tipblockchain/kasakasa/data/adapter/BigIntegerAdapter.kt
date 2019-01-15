package io.tipblockchain.kasakasa.data.adapter

import com.google.gson.JsonSyntaxException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.math.BigInteger

class BigIntegerAdapter: TypeAdapter<BigInteger>() {

    override fun write(out: JsonWriter?, value: BigInteger?) {
        out?.value(value.toString())
    }

    override fun read(value: JsonReader?): BigInteger? {
        if (value?.peek() === JsonToken.NULL) {
            value?.nextNull()
            return null
        }
        try {
            return BigInteger(value?.nextString())
        } catch (e: NumberFormatException) {
            throw JsonSyntaxException(e)
        }
    }
}