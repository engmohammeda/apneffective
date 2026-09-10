package com.engmohammeda.apndoctor.ui.home

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.engmohammeda.apndoctor.domain.model.*
import com.engmohammeda.apndoctor.util.ApnIntentUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val phoneStatePermissionState = rememberPermissionState(
        permission = Manifest.permission.READ_PHONE_STATE
    )

    LaunchedEffect(phoneStatePermissionState.status) {
        viewModel.onPermissionResult(phoneStatePermissionState.status.isGranted)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("APN Doctor", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (!phoneStatePermissionState.status.isGranted) {
                PermissionCard(
                    onRequest = { phoneStatePermissionState.launchPermissionRequest() }
                )
            } else {
                if (uiState.simList.size > 1) {
                    SimSelector(
                        sims = uiState.simList,
                        selectedSim = uiState.selectedSim,
                        onSelect = { viewModel.selectSim(it) }
                    )
                    Spacer(Modifier.height(16.dp))
                }

                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        item {
                            SimCard(sim = uiState.selectedSim)
                        }
                        item {
                            NetworkCard(network = uiState.network)
                        }
                        item {
                            DiagnosisCard(diagnosis = uiState.diagnosis)
                        }
                        item {
                            ApnResultCard(
                                apn = uiState.recommendedApn,
                                capability = uiState.capabilityResult,
                                onOpenSettings = { ApnIntentUtils.openApnSettings(context) }
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.scan() },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("فحص الاتصال", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionCard(onRequest: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("صلاحيات مطلوبة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("يحتاج التطبيق إلى صلاحية قراءة حالة الهاتف للتعرف على الشرائح الموجودة في الجهاز لتشخيص الإعدادات بشكل صحيح.")
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRequest) {
                Text("منح الصلاحية")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimSelector(sims: List<SimInfo>, selectedSim: SimInfo?, onSelect: (SimInfo) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        sims.forEach { sim ->
            FilterChip(
                selected = sim == selectedSim,
                onClick = { onSelect(sim) },
                label = { Text("SIM ${sim.slotIndex + 1}") },
                leadingIcon = {
                    Icon(Icons.Default.SimCard, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            )
        }
    }
}

@Composable
fun SimCard(sim: SimInfo?) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SimCard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("الشريحة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Divider(Modifier.padding(vertical = 8.dp))
            if (sim != null) {
                Text(sim.carrierName, style = MaterialTheme.typography.bodyLarge)
                Text("MCC: ${sim.mcc}   MNC: ${sim.mnc}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            } else {
                Text("لم يتم التعرف على الشريحة", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun NetworkCard(network: NetworkInfo?) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CellTower, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(Modifier.width(8.dp))
                Text("الشبكة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Divider(Modifier.padding(vertical = 8.dp))
            if (network != null) {
                Text("${network.operatorName ?: "غير معروف"} - ${network.networkType}", style = MaterialTheme.typography.bodyLarge)
                val statusColor = if (network.isConnected) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                Text(
                    text = if (network.isConnected) "متصل بالبيانات" else "غير متصل بالبيانات",
                    color = statusColor,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Text("لا توجد معلومات للشبكة", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun DiagnosisCard(diagnosis: ApnDiagnosis?) {
    if (diagnosis == null) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("نتيجة التشخيص", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Divider(Modifier.padding(vertical = 8.dp))
            
            DiagnosisRow(checked = diagnosis.simReady, text = "الشريحة جاهزة")
            DiagnosisRow(checked = diagnosis.operatorRecognized, text = "تم التعرف على المشغل")
            DiagnosisRow(checked = diagnosis.networkRegistered, text = "مسجل في الشبكة")
            DiagnosisRow(checked = diagnosis.apnKnown, text = "إعدادات APN معروفة")
            DiagnosisRow(checked = diagnosis.dataConnectionAvailable, text = "اتصال البيانات متاح")
            
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { diagnosis.confidence / 100f },
                modifier = Modifier.fillMaxWidth()
            )
            Text("نسبة دقة التشخيص: ${diagnosis.confidence}%", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun DiagnosisRow(checked: Boolean, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Icon(
            imageVector = if (checked) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (checked) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
fun ApnResultCard(apn: Apn?, capability: ApnWriteCapabilityResult?, onOpenSettings: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("إعدادات APN المقترحة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Divider(Modifier.padding(vertical = 8.dp))
            if (apn != null) {
                Text("الاسم: ${apn.operatorName}")
                Text("APN: ${apn.apn}", fontWeight = FontWeight.Bold)
                Text("النوع: ${apn.apnType}")
                Text("البروتوكول: ${apn.protocol}")
                
                Spacer(Modifier.height(16.dp))
                
                if (capability?.capability == ApnWriteCapability.DIRECTLY_SUPPORTED) {
                    Button(onClick = { /* Implement directly */ }, modifier = Modifier.fillMaxWidth()) {
                        Text("تطبيق تلقائي")
                    }
                } else {
                    Text(
                        "التعديل التلقائي غير مدعوم على جهازك لدواعي أمنية من نظام الأندرويد. الرجاء إدخالها يدوياً.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("فتح إعدادات APN")
                    }
                }
            } else {
                Text("لم يتم العثور على إعدادات متطابقة لهذه الشريحة.")
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) {
                    Text("التحقق اليدوي")
                }
            }
        }
    }
}
