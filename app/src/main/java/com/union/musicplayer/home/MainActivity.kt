package com.union.musicplayer.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.musickplayer.R
import com.seed.network.RequestResult
import com.seed.network.SeedNetEngine
import kotlinx.coroutines.*
import retrofit2.Call
import rx.Observable

class MainActivity : AppCompatActivity() {
    val mainScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainScope.launch {
            try {
                val testNet = testNet()
                Log.e("ABABAB", "${testNet}")
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

    }

    suspend fun testNet(): TestBean {
        return withContext(Dispatchers.IO) {
            SeedNetEngine.ins().get(TestInterKt::class.java).test()
        }
    }
}