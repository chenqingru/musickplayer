package com.seed.network

open class BaseRepositoryService {
    suspend fun <T> doRequest(block: suspend () -> RequestResult<T>?): RequestResult<T>? {
        var result: RequestResult<T>?
        try {
            result = block()
        } catch (e: Exception) {
            e.printStackTrace()
            result = null
        }
        return result
    }
}