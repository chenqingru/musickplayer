package com.union.musicplayer.home

import androidx.lifecycle.ViewModel
import com.seed.network.RequestResult
import com.seed.network.SeedNetEngine
import com.seed.network.SeedNetUtils
import com.seed.utils.NonStickyLiveData

class MainViewModel : ViewModel() {
    val testLiveData: NonStickyLiveData<RequestResult<List<TestBean.BookData>>> = NonStickyLiveData<RequestResult<List<TestBean.BookData>>>()

    fun requestTest() {
        SeedNetUtils.requestNet({
            SeedNetEngine.ins().get(TestInterKt::class.java).test()
        }, testLiveData)
    }
}