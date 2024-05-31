package com.example.network.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import java.util.Date

class DateAdapter {
    @ToJson
    fun toJson(writer: JsonWriter, value: Date?) {
        value?.let {
            writer.value(it.time)
        } ?: writer.nullValue()
    }

    @FromJson
    fun fromJson(reader: JsonReader): Date? {
        return if (reader.peek() != JsonReader.Token.NULL) {
            Date(reader.nextLong())
        } else {
            reader.nextNull<Date>()
            null
        }
    }
}
