package com.rpea.qlob.models

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
data class RecitersResponse(
    val reciters: List<Reciter>
)

@JsonClass(generateAdapter = true)
data class Reciter(
    val id: Int,
    val name: String,
    val letter: String,
    val moshaf: List<Moshaf>
)

@JsonClass(generateAdapter = true)
data class Moshaf(
    val id: Int,
    val name: String,
    val server: String,
    @Json(name = "surah_total") val surahTotal: Int,
    @Json(name = "moshaf_type") val moshafType: Int,
    @Json(name = "surah_list") val surahList: String
)

@JsonClass(generateAdapter = true)
data class SuwarResponse(
    val suwar: List<Surah>
)

@JsonClass(generateAdapter = true)
data class Surah(
    val id: Int,
    val name: String,
    @Json(name = "start_page") val startPage: Int,
    @Json(name = "end_page") val endPage: Int,
    val makkia: Int,
    val type: Int
)

@JsonClass(generateAdapter = true)
data class PrayerTimesResponse(
    val code: Int,
    val status: String,
    val data: PrayerData
)

@JsonClass(generateAdapter = true)
data class PrayerData(
    val timings: Timings
)

@JsonClass(generateAdapter = true)
data class Timings(
    val Fajr: String,
    val Dhuhr: String,
    val Asr: String,
    val Maghrib: String,
    val Isha: String
)
