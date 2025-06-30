package com.raafi.muhasabahharian

import com.raafi.muhasabahharian.data.local.entity.MuhasabahEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class MuhasabahEntityTest {

    @Test
    fun `muhasabah entity should have correct properties`() {
        val entity = MuhasabahEntity(
            id = 1,
            date = "2023-10-10",
            type = "Harian",
            activity = "Membaca",
            obstacle = "Kurang konsentrasi",
            note = "Harus lebih fokus",
            mood = "Baik",
            score = 80
        )

        assertEquals(1, entity.id)
        assertEquals("2023-10-10", entity.date)
        assertEquals("Harian", entity.type)
        assertEquals("Membaca", entity.activity)
        assertEquals("Kurang konsentrasi", entity.obstacle)
        assertEquals("Harus lebih fokus", entity.note)
        assertEquals("Baik", entity.mood)
        assertEquals(80, entity.score)
    }

    @Test
    fun `muhasabah entity default id should be zero`() {
        val entity = MuhasabahEntity(
            date = "2023-10-10",
            type = "Harian",
            activity = "Membaca",
            obstacle = "Kurang konsentrasi",
            note = "Harus lebih fokus",
            mood = "Baik",
            score = 80
        )

        assertEquals(0, entity.id)
    }
}