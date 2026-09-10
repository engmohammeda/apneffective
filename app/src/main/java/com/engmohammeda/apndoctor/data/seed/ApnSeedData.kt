package com.engmohammeda.apndoctor.data.seed

import com.engmohammeda.apndoctor.data.local.ApnEntity

object ApnSeedData {
    val initialData = listOf(
        ApnEntity(
            operatorName = "Yemen Mobile",
            countryCode = "YE",
            mcc = "421",
            mnc = "11",
            apn = "ymdata",
            username = "ym",
            password = "ym",
            authType = null,
            apnType = "default,supl",
            protocol = "IPV4V6",
            roamingProtocol = "IPV4V6",
            bearer = null,
            mvnoType = null,
            mvnoMatchData = null,
            notes = "Yemen Mobile 3G/4G"
        ),
        ApnEntity(
            operatorName = "SabaFon",
            countryCode = "YE",
            mcc = "421",
            mnc = "01",
            apn = "internet",
            username = "",
            password = "",
            authType = null,
            apnType = "default,supl",
            protocol = "IPV4",
            roamingProtocol = "IPV4",
            bearer = null,
            mvnoType = null,
            mvnoMatchData = null,
            notes = "SabaFon 3G/4G"
        ),
        ApnEntity(
            operatorName = "YOU",
            countryCode = "YE",
            mcc = "421",
            mnc = "02",
            apn = "internet",
            username = "",
            password = "",
            authType = null,
            apnType = "default,supl",
            protocol = "IPV4",
            roamingProtocol = "IPV4",
            bearer = null,
            mvnoType = null,
            mvnoMatchData = null,
            notes = "YOU (formerly MTN Yemen)"
        ),
        ApnEntity(
            operatorName = "Y Telecom",
            countryCode = "YE",
            mcc = "421",
            mnc = "04",
            apn = "internet",
            username = "",
            password = "",
            authType = null,
            apnType = "default,supl",
            protocol = "IPV4",
            roamingProtocol = "IPV4",
            bearer = null,
            mvnoType = null,
            mvnoMatchData = null,
            notes = "Y Telecom"
        )
    )
}
