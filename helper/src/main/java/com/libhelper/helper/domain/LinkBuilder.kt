package com.libhelper.helper.domain

import android.content.Context
import android.net.Uri
import com.libhelper.helper.caesar
import com.libhelper.helper.data.apps.AppsRepository
import com.libhelper.helper.data.facebook.FbDeepRepository
import com.libhelper.helper.data.phone.PhoneRepository
import com.libhelper.helper.data.referrer.ReferrerRepository
import com.libhelper.helper.data.remote.RemoteRepository
import com.libhelper.helper.data.remote.RemoteResult

class LinkBuilder(
    private val apps: AppsRepository,
    private val phone: PhoneRepository,
    private val referrer: ReferrerRepository,
    private val fb: FbDeepRepository,
    private val remote: RemoteRepository,
    private var baseUrl: String,
    private var id: String,
    private var token: String,
    private var key: String,
    private var domain: String,
) {
    class Builder {
        private var baseUrl: String = ""
        private var id: String = ""
        private var token: String = ""
        private var key: String = ""
        private var domain: String = ""
        fun setRemoteData(
            baseUrl: String,
            id: String,
            token: String,
            key: String,
            domain: String
        ): Builder {
            this.baseUrl = baseUrl
            this.id = id
            this.token = token
            this.key = key
            this.domain = domain
            return this
        }

        private var devKey: String = ""
        fun setAppsKey(key: String): Builder {
            devKey = key
            return this
        }

        private var afUserID: String = ""
        fun setAfUserId(afId: String): Builder {
            afUserID = afId
            return this
        }

        private lateinit var oneSignalSender: (String?, String) -> Unit
        fun setOneSignalSender(sender: (String?, String) -> Unit): Builder {
            oneSignalSender = sender
            return this
        }

        private lateinit var record: (Throwable) -> Unit
        fun setExceptionLogger(logger: (Throwable) -> Unit): Builder {
            record = logger
            return this
        }

        fun build(): LinkBuilder {
            return LinkBuilder(
                AppsRepository(devKey, afUserID, oneSignalSender, record),
                PhoneRepository(record),
                ReferrerRepository(record),
                FbDeepRepository(record),
                RemoteRepository(record),
                baseUrl, id, token, key, domain
            )
        }
    }


    suspend fun buildUrl(context: Context, decrypt: ((String) -> String)? = null): BuildResult {
        return when (val remote = remote.getData(baseUrl, id, token, key, domain)) {
            is RemoteResult.Error -> {
                BuildResult.Error
            }
            is RemoteResult.Success -> {
                val domain = (decrypt?.invoke(remote.data.final) ?: remote.data.final)
                val builder = Uri.Builder()
                builder.scheme("gssor".caesar())
                builder.authority(domain)
                builder.appendQueryParameter("ea_zoo_hc".caesar(), remote.data.id)
                builder.appendQueryParameter("ea_zs".caesar(), remote.data.token)
                apps.fetchData(context, builder) {
                    fb.getData(context, remote.data.id, remote.data.token)
                }
                phone.getData(context, builder)
                referrer.getData(context, remote.data.key, builder)
                BuildResult.Success(builder.build().toString())
            }
        }

    }
}