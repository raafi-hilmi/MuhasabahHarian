package com.raafi.muhasabahharian.presentation.muhasabah

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raafi.muhasabahharian.data.local.entity.MuhasabahEntity
import com.raafi.muhasabahharian.data.repository.MuhasabahRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.*

@HiltViewModel
class MuhasabahViewModel @Inject constructor(
    private val repository: MuhasabahRepository
) : ViewModel() {

    fun saveReflection(
        type: String,
        answers: Map<String, Any>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val entity = MuhasabahEntity(
                date = date,
                type = type,
                activity = answers["activity"] as? String ?: "",
                obstacle = answers["obstacle"] as? String ?: "",
                note = answers["note"] as? String ?: "",
                mood = answers["mood"] as? String ?: "😐",
                score = ((answers["score"] as? Float)?.times(10))?.toInt() ?: 5
            )

            repository.insertReflection(entity)
            onSuccess()
        }
    }
}
