package io.tipblockchain.kasakasa.data.serializer

import io.tipblockchain.kasakasa.data.db.Converters
import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


@Serializer(forClass = Date::class)
object DateSerializer: KSerializer<Date> {
    private val df: DateFormat = SimpleDateFormat(Converters.defaultDateFormat)

    override fun save(output: KOutput, obj: Date) {
        output.writeStringValue(df.format(obj))
    }

    override fun load(input: KInput): Date {
        return df.parse(input.readStringValue())
    }

    override val serialClassDesc: KSerialClassDesc
        get() = SerialClassDescImpl("DateSerializer")
}