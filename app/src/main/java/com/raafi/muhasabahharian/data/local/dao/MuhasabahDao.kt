package com.raafi.muhasabahharian.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.raafi.muhasabahharian.data.local.entity.MuhasabahEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MuhasabahDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(muhasabah: MuhasabahEntity): Long


    @Query("SELECT * FROM muhasabah ORDER BY date DESC")
    fun getAll(): Flow<List<MuhasabahEntity>>

    @Query("SELECT * FROM muhasabah WHERE id = :id")
    suspend fun getById(id: Int): MuhasabahEntity?

    @Delete
    suspend fun delete(muhasabah: MuhasabahEntity)

    @Query("DELETE FROM muhasabah WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM muhasabah")
    suspend fun deleteAll()

    @Update
    suspend fun update(muhasabah: MuhasabahEntity)
}