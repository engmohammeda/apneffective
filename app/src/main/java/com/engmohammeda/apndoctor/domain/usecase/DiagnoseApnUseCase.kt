package com.engmohammeda.apndoctor.domain.usecase

import com.engmohammeda.apndoctor.domain.model.Apn
import com.engmohammeda.apndoctor.domain.model.ApnDiagnosis
import com.engmohammeda.apndoctor.domain.model.NetworkInfo
import com.engmohammeda.apndoctor.domain.model.SimInfo

class DiagnoseApnUseCase {
    operator fun invoke(sim: SimInfo?, network: NetworkInfo?, apns: List<Apn>): ApnDiagnosis {
        val simReady = sim != null
        val operatorRecognized = simReady && sim?.mcc?.isNotEmpty() == true
        val networkRegistered = network?.mcc?.isNotEmpty() == true
        val dataConnectionAvailable = network?.isConnected == true
        val apnKnown = apns.isNotEmpty()
        
        var confidence = 0
        if (operatorRecognized) {
            if (sim?.mcc == network?.mcc && sim?.mnc == network?.mnc) {
                confidence += 60
            } else if (sim?.mcc?.isNotEmpty() == true) {
                confidence += 40
            }
        }
        if (apnKnown) confidence += 40
        
        return ApnDiagnosis(
            simReady = simReady,
            operatorRecognized = operatorRecognized,
            networkRegistered = networkRegistered,
            dataConnectionAvailable = dataConnectionAvailable,
            apnKnown = apnKnown,
            currentApnMatches = null, // Standard apps cannot easily read APN DB
            confidence = confidence.coerceIn(0, 100)
        )
    }
}
