package com.raafi.muhasabahharian.data.repository

import com.raafi.muhasabahharian.data.local.entity.MuhasabahEntity
import kotlinx.coroutines.flow.Flow

interface MuhasabahRepository {
    suspend fun insertReflection(muhasabah: MuhasabahEntity)
    fun getAllReflections(): Flow<List<MuhasabahEntity>>
    suspend fun getReflectionById(id: Int): MuhasabahEntity?
    suspend fun deleteReflection(muhasabah: MuhasabahEntity)
    suspend fun deleteReflectionById(id: Int)
    suspend fun deleteAllReflections()
    suspend fun updateReflection(muhasabah: MuhasabahEntity)
    suspend fun refreshFromRemote()
}