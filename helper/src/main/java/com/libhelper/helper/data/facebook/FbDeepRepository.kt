package com.libhelper.helper.data.facebook

import android.content.Context
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FbDeepRepository(private val record: (Throwable) -> Unit) {
    suspend fun getData(
        context: Context,
        id: String,
        token: String,
    ): String {
        FacebookSdk.setApplicationId(id)
        FacebookSdk.setClientToken(token)
        @Suppress("DEPRECATION")
        FacebookSdk.sdkInitialize(context)
        FacebookSdk.setAdvertiserIDCollectionEnabled(true)
        FacebookSdk.setAutoInitEnabled(true)
        FacebookSdk.fullyInitialize()
        return suspendCancellableCoroutine { continuation ->
            try {
                AppLinkData.fetchDeferredAppLinkData(context) {
                    if (continuation.isActive) continuation.resume(
                        it?.targetUri?.toString() ?: ""
                    )
                }
            } catch (e: Exception) {
                record(e)
                if (continuation.isActive) continuation.resume("")
            }
        }
    }
}




