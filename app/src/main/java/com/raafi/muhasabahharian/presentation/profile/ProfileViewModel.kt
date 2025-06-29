package com.raafi.muhasabahharian.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raafi.muhasabahharian.data.repository.MuhasabahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    repo: MuhasabahRepository
) : ViewModel() {
    val activeDays: StateFlow<Int> =
        repo.getAllReflections().map { list -> list.map { it.date }.distinct().size }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val levelLabel: StateFlow<String> = activeDays.map { days ->
        when {
            days >= 21 -> "Reflektif Aktif"
            days >= 7  -> "Konsisten"
            else       -> "Pemula"
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "Pemula")
}
