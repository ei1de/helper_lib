package com.libhelper.helper.data.remote


data class RemoteData(
    val id: String,
    val token: String,
    val key: String,
    val final: String,
)

sealed interface RemoteResult {
    object Error : RemoteResult
    data class Success(val data:RemoteData) : RemoteResult
}