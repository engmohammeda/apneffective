package com.engmohammeda.apndoctor.telephony

import android.content.Context
import android.telephony.SubscriptionManager
import com.engmohammeda.apndoctor.domain.model.SimInfo

class SubscriptionManagerWrapper(private val context: Context) {
    private val subscriptionManager =
        context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

    fun getActiveSims(): List<SimInfo> {
        try {
            val subscriptions = subscriptionManager.activeSubscriptionInfoList ?: return emptyList()
            return subscriptions.map { subInfo ->
                SimInfo(
                    slotIndex = subInfo.simSlotIndex,
                    subscriptionId = subInfo.subscriptionId,
                    carrierName = subInfo.carrierName?.toString() ?: "Unknown",
                    mcc = subInfo.mccString ?: "",
                    mnc = subInfo.mncString ?: "",
                    carrierId = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        subInfo.carrierId
                    } else null,
                    isDataEnabled = true,
                    networkRoaming = subscriptionManager.isNetworkRoaming(subInfo.subscriptionId)
                )
            }
        } catch (e: SecurityException) {
            // Missing READ_PHONE_STATE permission
            return emptyList()
        }
    }
}
