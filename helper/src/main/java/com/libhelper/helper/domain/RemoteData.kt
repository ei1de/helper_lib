package com.libhelper.helper.domain


data class RemoteData(
    val id: String,
    val token: String,
    val key: String,
    val domain: String,
)

sealed interface RemoteResult {
    object Error : RemoteResult
    data class Success(val data:RemoteData) : RemoteResult
}