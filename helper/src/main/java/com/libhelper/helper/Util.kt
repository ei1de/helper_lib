package com.libhelper.helper

import android.content.Context
import com.appsflyer.AppsFlyerLib

fun String.caesar(shift: Int = 25): String {
    val decryptedMessage = StringBuilder()
    for (char in this) {
        if (char.isLetter()) {
            val baseChar = if (char.isLowerCase()) 'a' else 'A'
            val shiftedChar =
                ((char.code - baseChar.code - shift + 26) % 26 + baseChar.code).toChar()
            decryptedMessage.append(shiftedChar)
        } else {
            decryptedMessage.append(char)
        }
    }
    return decryptedMessage.toString()
}
fun Context.getAfUserID(record:(Throwable)->Unit):String{
    val afUserId = try {
        AppsFlyerLib.getInstance().getAppsFlyerUID(this).toString()
    } catch (e: Exception) {
        record(e)
        null
    }
    return afUserId.toString()
}