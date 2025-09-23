package com.hg.qynnlauncher.utils.serialization

import com.hg.qynnlauncher.utils.RawRepresentable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class StringEnumWriteOnlySerializer : KSerializer<RawRepresentable<String>>
{
    override val descriptor= PrimitiveSerialDescriptor("StringEnum", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: RawRepresentable<String>)
    {
        encoder.encodeString(value.rawValue)
    }

    override fun deserialize(decoder: Decoder): RawRepresentable<String>
    {
        TODO("Not yet implemented")
    }
}