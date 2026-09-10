package com.engmohammeda.apndoctor.domain.model

data class SimInfo(
    val slotIndex: Int,
    val subscriptionId: Int,
    val carrierName: String,
    val mcc: String,
    val mnc: String,
    val carrierId: Int? = null,
    val isDataEnabled: Boolean = false,
    val networkRoaming: Boolean = false
)
