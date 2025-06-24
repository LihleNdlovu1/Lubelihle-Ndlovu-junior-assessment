package com.example.personapulse.network

import retrofit2.http.GET
import retrofit2.http.Query

data class GeocodeResult(
    val lat: String,
    val lon: String,
    val display_name: String
)

interface GeocodingService {
    @GET("search")
    suspend fun searchCity(
        @Query("q") cityName: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 1
    ): List<GeocodeResult>
}
