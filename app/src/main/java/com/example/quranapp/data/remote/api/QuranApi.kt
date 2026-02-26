package com.example.quranapp.data.remote.api

import com.example.quranapp.data.remote.dto.QuranMetaResponse
import com.example.quranapp.data.remote.dto.SurahDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface QuranApi {
    @GET("meta")
    suspend fun getQuranMeta(): QuranMetaResponse

    @GET("surah/{id}/ar.alafasy")
    suspend fun getSurahWithAudio(@Path("id") surahId: Int): SurahDetailResponse
}
