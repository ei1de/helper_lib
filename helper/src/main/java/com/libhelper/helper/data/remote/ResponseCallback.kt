package com.libhelper.helper.data.remote

import kotlinx.coroutines.CancellableContinuation
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume

class ResponseCallback(
    private val service: RemoteService,
    private val cont: CancellableContinuation<RemoteResult>,
    private val id: String,
    private val token: String,
    private val key: String,
    private val domain: String,
    private val record: (Throwable) -> Unit
) : Callback<ResponseBody> {
    private var countOfRequests = 2
    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        if (response.isSuccessful) {
            val data = response.body()?.string()
            if (data != null) {
                val jsonData = JSONObject(data)
                val domain = jsonData.getString(domain)
                if(domain != ""){
                    val id = jsonData.getString(id)
                    val token = jsonData.getString(token)
                    val key = jsonData.getString(key)               
                    if (cont.isActive) cont.resume(
                        RemoteResult.Success(
                            RemoteData(
                                id,
                                token,
                                key,
                                domain
                            )
                        )
                    )
                }else{
                    if (cont.isActive) cont.resume(RemoteResult.Error)
                }
            } else {
                if (cont.isActive) cont.resume(RemoteResult.Error)
            }
        } else {
            if (countOfRequests > 0) {
                service.fetch().enqueue(this)
                countOfRequests--
            } else {
                if (cont.isActive) cont.resume(RemoteResult.Error)
            }
        }
    }

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        record(t)
        if (countOfRequests > 0) {
            service.fetch().enqueue(this)
            countOfRequests--
        } else {
            if (cont.isActive) cont.resume(RemoteResult.Error)
        }
    }

}
