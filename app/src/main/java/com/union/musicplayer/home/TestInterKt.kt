package com.union.musicplayer.home

import com.seed.network.RequestResult
import retrofit2.Call
import retrofit2.http.GET
import rx.Observable

interface TestInterKt {
    @GET("tree/json")
    suspend fun test(): TestBean
}