package com.engmohammeda.apndoctor.util

import android.content.Context
import android.content.Intent
import android.provider.Settings

object ApnIntentUtils {
    fun openApnSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APN_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to wireless settings if APN settings directly is not found
            try {
                val fallbackIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(fallbackIntent)
            } catch (ex: Exception) {
                // Ignore
            }
        }
    }
}
