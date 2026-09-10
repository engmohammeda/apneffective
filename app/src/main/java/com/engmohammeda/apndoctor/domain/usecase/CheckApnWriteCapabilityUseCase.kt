package com.engmohammeda.apndoctor.domain.usecase

import android.content.Context
import android.content.pm.ApplicationInfo
import com.engmohammeda.apndoctor.domain.model.ApnWriteCapability
import com.engmohammeda.apndoctor.domain.model.ApnWriteCapabilityResult

class CheckApnWriteCapabilityUseCase(private val context: Context) {
    operator fun invoke(): ApnWriteCapabilityResult {
        val isSystemApp = (context.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        return if (isSystemApp) {
            ApnWriteCapabilityResult(ApnWriteCapability.DIRECTLY_SUPPORTED, "System app detected")
        } else {
            ApnWriteCapabilityResult(ApnWriteCapability.NOT_SUPPORTED, "Standard app cannot write APN directly")
        }
    }
}
