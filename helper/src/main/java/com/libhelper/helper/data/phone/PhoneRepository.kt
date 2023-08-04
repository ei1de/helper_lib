package com.libhelper.helper.data.phone

import android.content.Context
import android.net.Uri.Builder
import android.os.BatteryManager
import android.provider.Settings
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.libhelper.helper.caesar

class PhoneRepository(private val record: (Throwable) -> Unit) {
    companion object {
        private val adId = "fnnfkd_zchc".caesar()
        private val adb = "zca".caesar()
        private val battery = "azssdqx".caesar()
        private val bundle = "atmckd".caesar()
    }

    fun getData(context: Context, builder: Builder) {
        val result = mutableMapOf<String, String>()
        result[adId] = try {
            AdvertisingIdClient.getAdvertisingIdInfo(context).id
        } catch (e: Exception) {
            record(e)
            null
        }.toString()
        result[battery] = try {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val batteryLevel =
                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            batteryLevel.toFloat()
        } catch (e: Exception) {
            record(e)
            100.0f
        }.toString()
        result[adb] = try {
            Settings.Secure.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
            ) != 0
        } catch (e: Exception) {
            record(e)
            true
        }.toString()

        result[bundle] = context.packageName
        result.forEach {
            builder.appendQueryParameter(it.key, it.value)
        }
    }

}