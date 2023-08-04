package com.libhelper.helper.data.apps

import android.content.Context
import android.net.Uri
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.libhelper.helper.Type
import com.libhelper.helper.caesar
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AppsRepository(
    private val devKey: String,
    private val afUserId: String,
    private val sendOneSignal: (String?, String) -> Unit,
    private val record: (Throwable) -> Unit,
) {
    private var data: MutableMap<String, Any>? = null
    suspend fun fetchData(context: Context, builder: Uri.Builder, getDeep: suspend () -> String) {
        data = suspendCancellableCoroutine { continuation ->
            try {
                AppsFlyerLib.getInstance()
                    .init(devKey, object : AppsFlyerConversionListener {
                        override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                            if (continuation.isActive) continuation.resume(p0)
                        }

                        override fun onConversionDataFail(p0: String?) {
                            if (continuation.isActive) continuation.resume(null)
                        }

                        override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                            if (continuation.isActive) continuation.resume(null)
                        }

                        override fun onAttributionFailure(p0: String?) {
                            if (continuation.isActive) continuation.resume(null)
                        }

                    }, context)
                    .start(context)
            } catch (e: Exception) {
                record(e)
                if (continuation.isActive) continuation.resume(null)
            }
        }
        val deep = getDeep()
        build(builder, deep)
    }

    private fun build(builder: Uri.Builder, deep: String) {
        val subName = "rta".caesar()
        val result = mutableMapOf<String, String>()
        val campaign = if (deep != "") deep else data?.get(CAMPAIGN).toString()
        val subs = campaign.split("_")
        var subN = 1
        for (index in 0 until if (subs.size >= 11) subs.size else 11) {
            if (subN == 1) {
                val sub = subs.getOrNull(index).toString().split("://")
                result["$subName$subN"] = sub.getOrNull(1)
                    ?: if (sub.getOrNull(0) != "Mnmd".caesar())
                        sub.getOrNull(0).toString()
                    else
                        NULL
                subN += 1
            } else if (index != 1) {
                result["$subName$subN"] = subs.getOrNull(index) ?: ""
                subN += 1
            } else {
                val push = subs.getOrNull(1)
                sendOneSignal(push, afUserId)
                result[PUSH] = push.toString()
            }

        }
        result["${subName}10"] = Type.firstOpen.name
        result[CAMPAIGN] = campaign
        val params =
            listOf(
                "ldchz_rntqbd".caesar(),
                "ze_bgzmmdk".caesar(),
                "ze_rszstr".caesar(),
                "ze_zc".caesar(),
                "bzlozhfm_hc".caesar(),
                "zcrds_hc".caesar(),
                "zcrds".caesar()
            )
        params.forEach {
            result[it] = data?.get(it).toString()
        }
        result[AD_ID] = data?.get(ADGROUP_ID).toString()
        result[DEV_KEY] = devKey
        result[AF_USER_ID] = afUserId
        result.forEach {
            builder.appendQueryParameter(it.key, it.value)
        }

    }

    companion object {
        private val CAMPAIGN = "bzlozhfm".caesar()
        private val AF_USER_ID = "ze_trdqhc".caesar()
        private val DEV_KEY = "cdu_jdx".caesar()
        private val AD_ID = "zc_hc".caesar()
        private val ADGROUP_ID = "zcfqnto_hc".caesar()
        private val PUSH = "otrg".caesar()
        private val NULL = "mtkk".caesar()
    }
}