package com.raafi.muhasabahharian.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "muhasabah")
data class MuhasabahEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val type: String,
    val activity: String,
    val obstacle: String,
    val note: String,
    val mood: String,
    val score: Int
)