package io.tipblockchain.kasakasa.data.serializer

import io.tipblockchain.kasakasa.data.db.Converters
import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


@Serializer(forClass = Date::class)
object DateSerializer: KSerializer<Date?> {
    private val df: DateFormat = SimpleDateFormat(Converters.defaultDateFormat)

    override val descriptor: SerialDescriptor
        get() = SerialClassDescImpl("DateSerializer")

    override fun serialize(encoder: Encoder, obj: Date?) {
        encoder.encodeString(df.format(obj))
    }

    override fun deserialize(decoder: Decoder): Date? {
        return df.parse(decoder.decodeString())
    }
}
