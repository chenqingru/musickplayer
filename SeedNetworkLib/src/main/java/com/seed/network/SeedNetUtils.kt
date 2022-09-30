package com.seed.network

import com.seed.utils.NonStickyLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SeedNetUtils {


    companion object {
       private val mainScope = MainScope()
        fun <T> requestNet(block: suspend () -> T, liveData: NonStickyLiveData<T>) {
            mainScope.launch {
                var result: T? = null
                try {
                    result = withContext(Dispatchers.IO) {
                        block()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                liveData.postValue(result)
            }
        }
    }


}