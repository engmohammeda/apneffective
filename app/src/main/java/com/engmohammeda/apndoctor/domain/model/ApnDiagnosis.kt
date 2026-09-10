package com.engmohammeda.apndoctor.domain.model

data class ApnDiagnosis(
    val simReady: Boolean,
    val operatorRecognized: Boolean,
    val networkRegistered: Boolean,
    val dataConnectionAvailable: Boolean,
    val apnKnown: Boolean,
    val currentApnMatches: Boolean?,
    val confidence: Int
)

enum class ApnWriteCapability {
    DIRECTLY_SUPPORTED,
    SYSTEM_APP_REQUIRED,
    ROOT_REQUIRED,
    NOT_SUPPORTED,
    UNKNOWN
}

data class ApnWriteCapabilityResult(
    val capability: ApnWriteCapability,
    val reason: String
)
