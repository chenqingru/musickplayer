package com.union.musicplayer.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seed.network.RequestResult
import com.union.musicplayer.home.repository.TestRepositoryService
import com.union.musicplayer.home.retrofit.BookData
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _itestService = TestRepositoryService()


    public val testLiveDataSuccessful = MutableLiveData<RequestResult<List<BookData>>>()
    public val testLiveDataFail = MutableLiveData<RequestResult<List<BookData>>>()

    fun requestTest() {
        viewModelScope.launch {
            val responseResult = _itestService.requestTest()
            if(responseResult?.isSuccess==true){
                //do some things
                testLiveDataSuccessful.postValue(responseResult)
            }else{
                testLiveDataFail.postValue(responseResult)
            }
        }
    }


}