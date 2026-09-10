package com.engmohammeda.apndoctor.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.engmohammeda.apndoctor.data.local.AppDatabase
import com.engmohammeda.apndoctor.data.repository.ApnRepository
import com.engmohammeda.apndoctor.domain.model.*
import com.engmohammeda.apndoctor.domain.usecase.CheckApnWriteCapabilityUseCase
import com.engmohammeda.apndoctor.domain.usecase.DiagnoseApnUseCase
import com.engmohammeda.apndoctor.telephony.SubscriptionManagerWrapper
import com.engmohammeda.apndoctor.telephony.TelephonyManagerWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val simList: List<SimInfo> = emptyList(),
    val selectedSim: SimInfo? = null,
    val network: NetworkInfo? = null,
    val recommendedApn: Apn? = null,
    val diagnosis: ApnDiagnosis? = null,
    val capabilityResult: ApnWriteCapabilityResult? = null,
    val isLoading: Boolean = false,
    val hasPhoneStatePermission: Boolean = false,
    val applyMessage: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // Simple manual DI for now
    private val db = androidx.room.Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "apn_database"
    ).build()
    
    private val apnRepository = ApnRepository(db.apnDao())
    private val subscriptionManagerWrapper = SubscriptionManagerWrapper(application)
    private val telephonyManagerWrapper = TelephonyManagerWrapper(application)
    private val diagnoseApnUseCase = DiagnoseApnUseCase()
    private val checkApnWriteCapabilityUseCase = CheckApnWriteCapabilityUseCase(application)
    private val insertApnUseCase = com.engmohammeda.apndoctor.domain.usecase.InsertApnUseCase(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            apnRepository.populateInitialDataIfNeeded()
        }
    }

    fun onPermissionResult(granted: Boolean) {
        _uiState.update { it.copy(hasPhoneStatePermission = granted) }
        if (granted) {
            scan()
        }
    }

    fun selectSim(simInfo: SimInfo) {
        _uiState.update { it.copy(selectedSim = simInfo) }
        diagnose()
    }

    fun applyApn() {
        val sim = _uiState.value.selectedSim ?: return
        val apn = _uiState.value.recommendedApn ?: return
        
        viewModelScope.launch {
            val result = insertApnUseCase.invoke(apn, sim.subscriptionId)
            _uiState.update { it.copy(applyMessage = result.getOrNull() ?: result.exceptionOrNull()?.message) }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(applyMessage = null) }
    }

    fun scan() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val sims = subscriptionManagerWrapper.getActiveSims()
            val selected = sims.firstOrNull() // Default to first available SIM
            _uiState.update {
                it.copy(
                    simList = sims,
                    selectedSim = selected,
                    capabilityResult = checkApnWriteCapabilityUseCase()
                )
            }
            if (selected != null) {
                diagnose()
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun diagnose() {
        val currentSim = _uiState.value.selectedSim ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val networkInfo = telephonyManagerWrapper.getNetworkInfo(currentSim.subscriptionId)
            
            // Search APN database based on SIM/Network MCC/MNC
            val targetMcc = if (networkInfo.mcc?.isNotEmpty() == true) networkInfo.mcc else currentSim.mcc
            val targetMnc = if (networkInfo.mnc?.isNotEmpty() == true) networkInfo.mnc else currentSim.mnc
            
            val matchingApns = if (targetMcc.isNotEmpty() && targetMnc.isNotEmpty()) {
                apnRepository.getApnsForOperator(targetMcc, targetMnc)
            } else {
                emptyList()
            }
            
            val recommended = matchingApns.firstOrNull()
            
            val diagnosis = diagnoseApnUseCase(currentSim, networkInfo, matchingApns)
            
            _uiState.update {
                it.copy(
                    network = networkInfo,
                    recommendedApn = recommended,
                    diagnosis = diagnosis,
                    isLoading = false
                )
            }
        }
    }
}
