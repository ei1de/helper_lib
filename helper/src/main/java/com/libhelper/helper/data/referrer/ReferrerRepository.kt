package com.libhelper.helper.data.referrer

import android.content.Context
import android.net.Uri
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.libhelper.helper.caesar
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import java.net.URLDecoder
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.coroutines.resume

class ReferrerRepository(private val record: (Throwable) -> Unit) {
    suspend fun getData(context: Context, key: String, builder: Uri.Builder) {
        val data = suspendCancellableCoroutine { continuation ->
            val referrer = InstallReferrerClient.newBuilder(context).build()
            try {
                referrer.startConnection(object : InstallReferrerStateListener {
                    override fun onInstallReferrerSetupFinished(responseCode: Int) {
                        if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                            if (continuation.isActive) {
                                continuation.resume(referrer.installReferrer.installReferrer)
                                referrer.endConnection()
                            }
                        } else {
                            if (continuation.isActive) {
                                continuation.resume(null)
                                referrer.endConnection()
                            }
                        }
                    }

                    override fun onInstallReferrerServiceDisconnected() {
                        if (continuation.isActive) {
                            continuation.resume(null)
                            referrer.endConnection()
                        }
                    }
                })
            } catch (e: Exception) {
                record(e)
                if (continuation.isActive) continuation.resume(null)
            }
        }
        fetchId(data, key, builder)
    }

    private fun fetchId(data:String?, key: String, builder: Uri.Builder) {
        builder.appendQueryParameter(accountId, if (data != null) {
            try {
                val utmC = data.split(utmContent).getOrNull(1)
                if (utmC != null) {
                    val decodedReferrerData = URLDecoder.decode(
                        utmC,
                        utf8
                    )
                    val source =
                        JSONObject(JSONObject(decodedReferrerData)[source].toString())
                    val dataS = source[dataP]
                    val nonce = source[nonce]
                    val specKey = SecretKeySpec(
                        key.chunked(2).map { it.toInt(16).toByte() }.toByteArray(),
                        "ZDR/FBL/MnOzcchmf".caesar()
                    )
                    val nonceSpec =
                        IvParameterSpec(
                            nonce.toString().chunked(2).map { it.toInt(16).toByte() }
                                .toByteArray())
                    val cipher = Cipher.getInstance("ZDR/FBL/MnOzcchmf".caesar())
                    cipher.init(Cipher.DECRYPT_MODE, specKey, nonceSpec)
                    val message =
                        cipher.doFinal(dataS.toString().chunked(2).map { it.toInt(16).toByte() }
                            .toByteArray())
                    val res = JSONObject(String(message))
                    res.get(accountId).toString()
                } else {
                    nullStr
                }
            } catch (_: Exception) {
                nullStr
            }
        } else {
            nullStr
        })

    }

    companion object {
        private val utmContent = "tsl_bnmsdms=".caesar()
        private val accountId = "zbbntms_hc".caesar()
        private val utf8 = "tse-8".caesar()
        private val nullStr = "mtkk".caesar()
        private val source = "rntqbd".caesar()
        private val dataP = "czsz".caesar()
        private val nonce = "mnmbd".caesar()
    }
}