package com.libhelper.helper.data.remote

import androidx.annotation.Keep
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET


@Keep
interface RemoteService {
    @GET("js.json")
    fun fetch(): Call<ResponseBody>
}