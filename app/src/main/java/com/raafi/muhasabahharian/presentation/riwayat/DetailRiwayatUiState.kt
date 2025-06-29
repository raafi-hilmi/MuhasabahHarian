package com.raafi.muhasabahharian.presentation.riwayat

import com.raafi.muhasabahharian.data.local.entity.MuhasabahEntity

data class DetailRiwayatUiState(
    val isLoading: Boolean = false,
    val data: MuhasabahEntity? = null,
    val error: String? = null
)
