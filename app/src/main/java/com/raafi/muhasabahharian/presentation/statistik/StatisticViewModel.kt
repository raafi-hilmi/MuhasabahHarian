package com.raafi.muhasabahharian.presentation.statistik

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
class StatistikViewModel @Inject constructor(
    private val repository: MuhasabahRepository
) : ViewModel() {

    private val _refleksiList = MutableStateFlow<List<MuhasabahEntity>>(emptyList())
    val refleksiList: StateFlow<List<MuhasabahEntity>> = _refleksiList.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllReflections().collect {
                _refleksiList.value = it
            }
        }
    }

    fun getFilteredByType(type: String): List<MuhasabahEntity> {
        return _refleksiList.value.filter { it.type.equals(type, ignoreCase = true) }
    }
}
