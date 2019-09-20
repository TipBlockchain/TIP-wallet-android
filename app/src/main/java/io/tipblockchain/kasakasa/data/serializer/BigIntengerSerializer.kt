package io.tipblockchain.kasakasa.data.serializer

import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import java.math.BigInteger

@Serializer(forClass = BigInteger::class)
object BigIntegerSerializer: KSerializer<BigInteger> {

    override val descriptor: SerialDescriptor
        get() = SerialClassDescImpl("BigIntegerSerializer")

    override fun serialize(encoder: Encoder, obj: BigInteger) {
        encoder.encodeString(obj.toString(10))
    }

    override fun deserialize(decoder: Decoder): BigInteger {
        return BigInteger(decoder.decodeString(), 10)
    }
}