package com.raafi.muhasabahharian.data.repository

import android.util.Log
import com.raafi.muhasabahharian.data.local.dao.MuhasabahDao
import com.raafi.muhasabahharian.data.local.entity.MuhasabahEntity
import com.raafi.muhasabahharian.data.remote.RemoteMuhasabahDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MuhasabahRepositoryImpl @Inject constructor(
    private val dao: MuhasabahDao,
    private val remoteDS: RemoteMuhasabahDataSource
) : MuhasabahRepository {
    override suspend fun insertReflection(muhasabah: MuhasabahEntity) {
        val newId = dao.insert(muhasabah).toInt()
        val entityWithId = muhasabah.copy(id = newId)

        runCatching { remoteDS.upsert(entityWithId) }
            .onFailure { e -> Log.e("Repository", "Firestore insert failed", e) }
    }
    override fun getAllReflections(): Flow<List<MuhasabahEntity>> {
        return dao.getAll()
    }
    override suspend fun getReflectionById(id: Int): MuhasabahEntity? {
        return dao.getById(id)
    }
    override suspend fun deleteReflection(muhasabah: MuhasabahEntity) {
        dao.delete(muhasabah)
    }
    override suspend fun deleteReflectionById(id: Int) {
        dao.deleteById(id)
        runCatching { remoteDS.delete(id) }
            .onFailure { e -> Log.e("Repository", "Firestore delete by id failed", e) }

    }
    override suspend fun deleteAllReflections() {
        dao.deleteAll()
        runCatching { remoteDS.deleteAll() }
            .onFailure { e -> Log.e("Repository", "Firestore delete failed", e) }
    }
    override suspend fun updateReflection(muhasabah: MuhasabahEntity) {
        dao.update(muhasabah)
        runCatching { remoteDS.upsert(muhasabah) }
            .onFailure { e -> Log.e("Repository", "Firestore update failed", e) }
    }
    override suspend fun refreshFromRemote() {
        runCatching {
            val remoteList = remoteDS.fetchAll()
            dao.deleteAll()
            dao.insertAll(remoteList)
        }.onFailure { e ->
            Log.e("Repository", "Sync-fetchAll failed", e)
        }
    }
}