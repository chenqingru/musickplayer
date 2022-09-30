package com.union.musicplayer.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import com.musickplayer.R
import com.union.musicplayer.home.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private val mainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel.testLiveDataSuccessful.observe(this) { result ->
            if (result != null) {
                Log.e("ABABAB", "$result")
            }
        }
        mainViewModel.requestTest()
    }


}