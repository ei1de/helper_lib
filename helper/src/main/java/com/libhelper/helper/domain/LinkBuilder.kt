package com.libhelper.helper.domain

import android.content.Context
import android.net.Uri
import com.libhelper.helper.caesar
import com.libhelper.helper.data.apps.AppsRepository
import com.libhelper.helper.data.facebook.FbDeepRepository
import com.libhelper.helper.data.phone.PhoneRepository
import com.libhelper.helper.data.referrer.ReferrerRepository

class LinkBuilder(
    private val apps: AppsRepository,
    private val phone: PhoneRepository,
    private val referrer: ReferrerRepository,
    private val fb: FbDeepRepository,
    private val remote: suspend ()->RemoteResult,
) {
    class Builder {
        private var devKey: String = ""
        fun setAppsKey(key: String): Builder {
            devKey = key
            return this
        }

        private lateinit var record: (Throwable) -> Unit
        fun setExceptionLogger(logger: (Throwable) -> Unit): Builder {
            record = logger
            return this
        }

        private lateinit var remoteCallback:(suspend ()->RemoteResult)
        fun setRemoteConfigFetcher(remoteC:suspend ()->RemoteResult):Builder{
            remoteCallback = remoteC
            return this
        }

        fun build(): LinkBuilder {
            return LinkBuilder(
                AppsRepository(devKey, record),
                PhoneRepository(record),
                ReferrerRepository(record),
                FbDeepRepository(record),
                remoteCallback,
            )
        }
    }


    suspend fun buildUrl(context: Context, appsId:String): BuildResult {
        return when (val remote = remote()) {
            is RemoteResult.Error -> {
                BuildResult.Error
            }
            is RemoteResult.Success -> {
                val domain = remote.data.domain
                val builder = Uri.Builder()
                builder.scheme("gssor".caesar())
                builder.authority(domain)
                builder.appendQueryParameter("ea_zoo_hc".caesar(), remote.data.id)
                builder.appendQueryParameter("ea_zs".caesar(), remote.data.token)
                val push = apps.fetchData(context, builder, appsId) {
                    fb.getData(context, remote.data.id, remote.data.token)
                }
                phone.getData(context, builder)
                referrer.getData(context, remote.data.key, builder)
                BuildResult.Success(builder.build().toString(), push)
            }
        }

    }
}