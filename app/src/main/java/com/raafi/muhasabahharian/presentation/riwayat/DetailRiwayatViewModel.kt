package com.raafi.muhasabahharian.presentation.riwayat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raafi.muhasabahharian.data.local.entity.MuhasabahEntity
import com.raafi.muhasabahharian.data.repository.MuhasabahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailRiwayatViewModel @Inject constructor(
    private val repository: MuhasabahRepository
) : ViewModel() {

    private val _muhasabah = MutableStateFlow<MuhasabahEntity?>(null)
    val muhasabah: StateFlow<MuhasabahEntity?> = _muhasabah.asStateFlow()

    fun loadReflectionById(id: Int) {
        viewModelScope.launch {
            val result = repository.getReflectionById(id)
            _muhasabah.value = result
        }
    }

    fun updateReflection(updated: MuhasabahEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.insertReflection(updated)
            onSuccess()
        }
    }

    fun deleteReflection(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteReflectionById(id)
            onSuccess()
        }
    }
}
