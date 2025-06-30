package com.raafi.muhasabahharian.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raafi.muhasabahharian.data.local.dao.MuhasabahDao
import com.raafi.muhasabahharian.data.local.entity.MuhasabahEntity
import com.raafi.muhasabahharian.data.repository.MuhasabahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val dao: MuhasabahDao
) : ViewModel() {
    private val _muhasabah = MutableStateFlow<MuhasabahEntity?>(null)
    val muhasabah: StateFlow<MuhasabahEntity?> = _muhasabah.asStateFlow()

    fun clearReflection() {
        viewModelScope.launch {
            dao.deleteAll()
        }
    }
}