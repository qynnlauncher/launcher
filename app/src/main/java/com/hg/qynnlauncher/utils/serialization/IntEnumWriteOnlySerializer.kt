package com.hg.qynnlauncher.utils.serialization

import com.hg.qynnlauncher.utils.RawRepresentable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class IntEnumWriteOnlySerializer : KSerializer<RawRepresentable<Int>>
{
    override val descriptor = PrimitiveSerialDescriptor("IntEnum", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: RawRepresentable<Int>)
    {
        encoder.encodeInt(value.rawValue)
    }

    override fun deserialize(decoder: Decoder): RawRepresentable<Int>
    {
        TODO("Not yet implemented")
    }
}