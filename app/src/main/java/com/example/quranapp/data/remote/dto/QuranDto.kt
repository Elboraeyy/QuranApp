package com.example.quranapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class QuranMetaResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: QuranMetaData
)

data class QuranMetaData(
    @SerializedName("surahs") val surahs: SurahsMeta
)

data class SurahsMeta(
    @SerializedName("count") val count: Int,
    @SerializedName("references") val references: List<SurahReferenceDto>
)

data class SurahReferenceDto(
    @SerializedName("number") val number: Int,
    @SerializedName("name") val name: String,
    @SerializedName("englishName") val englishName: String,
    @SerializedName("englishNameTranslation") val englishNameTranslation: String,
    @SerializedName("numberOfAyahs") val numberOfAyahs: Int,
    @SerializedName("revelationType") val revelationType: String
)

data class SurahDetailResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: SurahDetailDto
)

data class SurahDetailDto(
    @SerializedName("number") val number: Int,
    @SerializedName("name") val name: String,
    @SerializedName("englishName") val englishName: String,
    @SerializedName("englishNameTranslation") val englishNameTranslation: String,
    @SerializedName("revelationType") val revelationType: String,
    @SerializedName("numberOfAyahs") val numberOfAyahs: Int,
    @SerializedName("ayahs") val ayahs: List<AyahDto>
)

data class AyahDto(
    @SerializedName("number") val number: Int,
    @SerializedName("audio") val audio: String?,
    @SerializedName("text") val text: String,
    @SerializedName("numberInSurah") val numberInSurah: Int,
    @SerializedName("juz") val juz: Int,
    @SerializedName("manzil") val manzil: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("ruku") val ruku: Int,
    @SerializedName("hizbQuarter") val hizbQuarter: Int
)
