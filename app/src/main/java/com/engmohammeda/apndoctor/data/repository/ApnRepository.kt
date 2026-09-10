package com.engmohammeda.apndoctor.data.repository

import com.engmohammeda.apndoctor.data.local.ApnDao
import com.engmohammeda.apndoctor.data.local.ApnEntity
import com.engmohammeda.apndoctor.data.seed.ApnSeedData
import com.engmohammeda.apndoctor.domain.model.Apn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ApnRepository(private val apnDao: ApnDao) {

    suspend fun getApnsForOperator(mcc: String, mnc: String): List<Apn> {
        return apnDao.findByMccMnc(mcc, mnc).map { it.toDomain() }
    }

    fun searchApns(query: String): Flow<List<Apn>> {
        return apnDao.search(query).map { list -> list.map { it.toDomain() } }
    }

    suspend fun populateInitialDataIfNeeded() {
        if (apnDao.getCount() == 0) {
            apnDao.insertAll(ApnSeedData.initialData)
        }
    }
}

fun ApnEntity.toDomain() = Apn(
    id = id,
    operatorName = operatorName,
    countryCode = countryCode,
    mcc = mcc,
    mnc = mnc,
    carrierId = carrierId,
    apn = apn,
    username = username,
    password = password,
    authType = authType,
    apnType = apnType,
    protocol = protocol,
    roamingProtocol = roamingProtocol,
    bearer = bearer,
    mvnoType = mvnoType,
    mvnoMatchData = mvnoMatchData,
    notes = notes
)

fun Apn.toEntity() = ApnEntity(
    id = id,
    operatorName = operatorName,
    countryCode = countryCode,
    mcc = mcc,
    mnc = mnc,
    carrierId = carrierId,
    apn = apn,
    username = username,
    password = password,
    authType = authType,
    apnType = apnType,
    protocol = protocol,
    roamingProtocol = roamingProtocol,
    bearer = bearer,
    mvnoType = mvnoType,
    mvnoMatchData = mvnoMatchData,
    notes = notes
)
