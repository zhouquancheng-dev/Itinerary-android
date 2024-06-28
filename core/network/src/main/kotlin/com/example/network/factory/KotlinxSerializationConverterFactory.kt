package com.example.network.factory

import com.example.model.BaseResponse
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class KotlinxSerializationConverterFactory(
    private val json: Json
) : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        val parameterizedType = type as? ParameterizedType
            ?: return null

        val rawType = parameterizedType.rawType
        if (rawType != BaseResponse::class.java) {
            return null
        }

        val dataType = parameterizedType.actualTypeArguments[0]
        val serializer = json.serializersModule.serializer(dataType)

        return KotlinxSerializationResponseBodyConverter(serializer, json)
    }
}

class KotlinxSerializationResponseBodyConverter<T>(
    private val serializer: KSerializer<T>,
    private val json: Json
) : Converter<ResponseBody, T> {
    override fun convert(value: ResponseBody): T? {
        val jsonString = value.string()
        return json.decodeFromString(serializer, jsonString)
    }
}