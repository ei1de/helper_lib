package com.libhelper.helper.data.remote

import com.google.gson.GsonBuilder
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RemoteRepository(
    private val record: (Throwable) -> Unit
) {
    suspend fun getData(
        urlBase: String,
        id: String,
        token: String,
        key: String,
        domain: String
    ): RemoteResult = suspendCancellableCoroutine {
        val service = Retrofit.Builder()
            .baseUrl(urlBase)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(
                OkHttpClient.Builder()
                    .readTimeout(25000, TimeUnit.MILLISECONDS)
                    .connectTimeout(25000, TimeUnit.MILLISECONDS)
                    .build()
            )
            .build().create(RemoteService::class.java)
        service.fetch().enqueue(ResponseCallback(service, it, id,token, key, domain, record))
    }
}