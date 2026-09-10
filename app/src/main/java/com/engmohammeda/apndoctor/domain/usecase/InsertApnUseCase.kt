package com.engmohammeda.apndoctor.domain.usecase

import android.content.ContentValues
import android.content.Context
import android.provider.Telephony
import com.engmohammeda.apndoctor.domain.model.Apn

class InsertApnUseCase(private val context: Context) {
    fun invoke(apn: Apn, subscriptionId: Int): Result<String> {
        return try {
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(Telephony.Carriers.NAME, apn.operatorName)
                put(Telephony.Carriers.APN, apn.apn)
                put(Telephony.Carriers.MCC, apn.mcc)
                put(Telephony.Carriers.MNC, apn.mnc)
                put(Telephony.Carriers.NUMERIC, "${apn.mcc}${apn.mnc}")
                put(Telephony.Carriers.TYPE, apn.apnType)
                put(Telephony.Carriers.PROTOCOL, apn.protocol)
                put(Telephony.Carriers.ROAMING_PROTOCOL, apn.roamingProtocol)
                
                apn.username?.takeIf { it.isNotEmpty() }?.let { put(Telephony.Carriers.USER, it) }
                apn.password?.takeIf { it.isNotEmpty() }?.let { put(Telephony.Carriers.PASSWORD, it) }
                
                // إضافة الإعداد للشريحة المحددة (دعم Dual SIM)
                put(Telephony.Carriers.SUBSCRIPTION_ID, subscriptionId)
            }
            
            val uri = resolver.insert(Telephony.Carriers.CONTENT_URI, values)
            if (uri != null) {
                Result.success("تمت إضافة نقطة الوصول بنجاح للشريحة المحددة!")
            } else {
                Result.failure(Exception("فشل إضافة نقطة الوصول، لا توجد استجابة من النظام."))
            }
        } catch (e: SecurityException) {
            Result.failure(SecurityException("يمنع النظام التعديل التلقائي. يجب أن يكون التطبيق مدمجاً في النظام (System App) أو بوجود روت."))
        } catch (e: Exception) {
            Result.failure(Exception("حدث خطأ غير متوقع: ${e.message}"))
        }
    }
}
