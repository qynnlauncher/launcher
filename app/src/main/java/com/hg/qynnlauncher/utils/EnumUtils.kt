package com.hg.qynnlauncher.utils

// https://stackoverflow.com/a/71578372/6796433
interface RawRepresentable<T>
{
    val rawValue: T
}

inline fun <reified TEnum, TBacking> valueOf(value: TBacking): TEnum?
        where TEnum : Enum<TEnum>, TEnum : RawRepresentable<TBacking>
{
    return enumValues<TEnum>().firstOrNull { it.rawValue == value }
}

inline fun <reified TEnum> intToEnumOrDefault(int: Int?, default: TEnum): TEnum
        where TEnum : Enum<TEnum>, TEnum : RawRepresentable<Int>
{
    return if (int == null)
        default
    else
        valueOf(int) ?: default
}

inline fun <reified TEnum> parseAsEnumOrNull(rawValue: String?): TEnum?
        where TEnum : Enum<TEnum>, TEnum : RawRepresentable<String>
{
    return if (rawValue == null)
        null
    else
        valueOf(rawValue)
}