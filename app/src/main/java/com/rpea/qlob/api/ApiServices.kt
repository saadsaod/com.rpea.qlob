package com.rpea.qlob.api

import com.rpea.qlob.models.PrayerTimesResponse
import com.rpea.qlob.models.RecitersResponse
import com.rpea.qlob.models.SuwarResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface Mp3QuranApi {
    @GET("reciters?language=ar")
    suspend fun getReciters(): RecitersResponse

    @GET("suwar?language=ar")
    suspend fun getSuwar(): SuwarResponse
}

interface AladhanApi {
    @GET("timingsByCity")
    suspend fun getPrayerTimes(
        @Query("city") city: String = "Jazan",
        @Query("country") country: String = "Saudi Arabia",
        @Query("method") method: Int = 4 // Umm Al-Qura University
    ): PrayerTimesResponse
}
