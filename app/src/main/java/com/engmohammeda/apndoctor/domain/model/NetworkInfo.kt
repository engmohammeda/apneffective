package com.engmohammeda.apndoctor.domain.model

data class NetworkInfo(
    val networkType: String,
    val isConnected: Boolean,
    val mcc: String?,
    val mnc: String?,
    val operatorName: String?
)
