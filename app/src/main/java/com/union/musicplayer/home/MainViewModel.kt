package com.union.musicplayer.home

import androidx.lifecycle.ViewModel
import com.seed.network.SeedNetEngine
import com.seed.network.SeedNetUtils
import com.seed.utils.NonStickyLiveData

class MainViewModel : ViewModel() {
    val testLiveData: NonStickyLiveData<TestBean> = NonStickyLiveData<TestBean>()

    fun requestTest() {
        SeedNetUtils.requestNet({
            SeedNetEngine.ins().get(TestInterKt::class.java).test()
        }, testLiveData)
    }
}