package com.engmohammeda.apndoctor.telephony

import android.content.Context
import android.telephony.TelephonyManager
import com.engmohammeda.apndoctor.domain.model.NetworkInfo

class TelephonyManagerWrapper(private val context: Context) {

    fun getTelephonyManagerForSubscription(subscriptionId: Int): TelephonyManager {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.createForSubscriptionId(subscriptionId)
    }

    fun getNetworkInfo(subscriptionId: Int): NetworkInfo {
        val tm = getTelephonyManagerForSubscription(subscriptionId)
        
        val networkOperator = tm.networkOperator
        val mcc = if (networkOperator != null && networkOperator.length >= 3) networkOperator.substring(0, 3) else null
        val mnc = if (networkOperator != null && networkOperator.length >= 3) networkOperator.substring(3) else null
        
        val networkTypeStr = try {
            when (tm.dataNetworkType) {
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_CDMA,
                TelephonyManager.NETWORK_TYPE_1xRTT,
                TelephonyManager.NETWORK_TYPE_IDEN -> "2G"
                TelephonyManager.NETWORK_TYPE_UMTS,
                TelephonyManager.NETWORK_TYPE_EVDO_0,
                TelephonyManager.NETWORK_TYPE_EVDO_A,
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_EVDO_B,
                TelephonyManager.NETWORK_TYPE_EHRPD,
                TelephonyManager.NETWORK_TYPE_HSPAP -> "3G"
                TelephonyManager.NETWORK_TYPE_LTE -> "4G LTE"
                TelephonyManager.NETWORK_TYPE_NR -> "5G NR"
                else -> "Unknown"
            }
        } catch (e: SecurityException) {
            "Unknown"
        }

        return NetworkInfo(
            networkType = networkTypeStr,
            isConnected = tm.dataState == TelephonyManager.DATA_CONNECTED,
            mcc = mcc,
            mnc = mnc,
            operatorName = tm.networkOperatorName
        )
    }
}
