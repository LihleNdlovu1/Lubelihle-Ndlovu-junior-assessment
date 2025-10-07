package com.PersonaPulse.personapulse.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//centralises all network configurations and also separates the clients for weather and geocoding.

object ApiClient {
    private const val BASE_URL = "https://api.open-meteo.com/"

    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }


    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val weatherService: WeatherService = retrofit.create(WeatherService::class.java)


    private val geoClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "PersonaPulse/1.0 (lihle@personapulse.com)")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(logger)
        .build()

    val geocodingService: GeocodingService = Retrofit.Builder()
        .baseUrl("https://nominatim.openstreetmap.org/")
        .client(geoClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GeocodingService::class.java)
}
