package com.example.network.service

import com.example.model.weather.RealtimeResponse
import com.example.network.key.Q_WEATHER_KEY
import com.example.network.url.REALTIME_WEATHER
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET(REALTIME_WEATHER)
    suspend fun realtimeWeather(
        @Query("location") location: String,
        @Query("key") key: String = Q_WEATHER_KEY,
        @Query("lang") lang: String = "zh",
        @Query("unit") unit: String = "m"
    ): RealtimeResponse

}