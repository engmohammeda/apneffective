package com.engmohammeda.apndoctor.domain.model

data class Apn(
    val id: Long = 0,
    val operatorName: String,
    val countryCode: String,
    val mcc: String,
    val mnc: String,
    val carrierId: Int? = null,
    val apn: String,
    val username: String? = null,
    val password: String? = null,
    val authType: String? = null,
    val apnType: String = "default,supl",
    val protocol: String = "IPV4V6",
    val roamingProtocol: String = "IPV4V6",
    val bearer: String? = null,
    val mvnoType: String? = null,
    val mvnoMatchData: String? = null,
    val notes: String? = null
)
