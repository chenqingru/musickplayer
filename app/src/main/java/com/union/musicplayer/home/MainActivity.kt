package com.union.musicplayer.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import com.musickplayer.R
import com.seed.network.RequestResult
import com.seed.network.SeedNetEngine
import kotlinx.coroutines.*
import retrofit2.Call
import rx.Observable

class MainActivity : AppCompatActivity() {
    private val mainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel.testLiveData.observe(this) { result ->
            if (result != null) {
                Log.e("ABABAB", "$result")
            }
        }
        mainViewModel.requestTest()
    }


}