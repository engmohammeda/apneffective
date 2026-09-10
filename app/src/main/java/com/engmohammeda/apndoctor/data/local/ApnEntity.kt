package com.engmohammeda.apndoctor.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "apn_profiles",
    indices = [
        Index(value = ["mcc", "mnc"]),
        Index(value = ["operatorName"])
    ]
)
data class ApnEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val operatorName: String,
    val countryCode: String,
    val mcc: String,
    val mnc: String,
    val carrierId: Int? = null,
    val apn: String,
    val username: String?,
    val password: String?,
    val authType: String?,
    val apnType: String,
    val protocol: String,
    val roamingProtocol: String,
    val bearer: String?,
    val mvnoType: String?,
    val mvnoMatchData: String?,
    val notes: String?
)
