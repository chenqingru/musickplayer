package com.union.musicplayer.home.repository

import com.seed.network.BaseRepositoryService
import com.seed.network.RequestResult
import com.seed.network.SeedNetEngine
import com.union.musicplayer.home.retrofit.BookData
import com.union.musicplayer.home.retrofit.TestInterKt

class TestRepositoryService : BaseRepositoryService() {
    suspend fun requestTest(): RequestResult<List<BookData>>? {
        return doRequest { SeedNetEngine.ins().get(TestInterKt::class.java).test() }
    }

}