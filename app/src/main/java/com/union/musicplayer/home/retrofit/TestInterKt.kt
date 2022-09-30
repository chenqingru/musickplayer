package com.union.musicplayer.home.retrofit

import com.seed.network.RequestResult
import retrofit2.http.GET

interface TestInterKt {
    @GET("tree/json")
    suspend fun test(): RequestResult<List<BookData>>
}