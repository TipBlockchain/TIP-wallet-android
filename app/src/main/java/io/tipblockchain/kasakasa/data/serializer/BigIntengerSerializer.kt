package io.tipblockchain.kasakasa.data.serializer

import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import java.math.BigInteger

@Serializer(forClass = BigInteger::class)
object BigIntegerSerializer: KSerializer<BigInteger> {
    override val serialClassDesc: KSerialClassDesc
        get() = SerialClassDescImpl("BigIntegerSerializer")

    override fun load(input: KInput): BigInteger {
        return BigInteger(input.readStringValue(), 10)
    }

    override fun save(output: KOutput, obj: BigInteger) {
        output.write(obj.toString(10))
    }
}