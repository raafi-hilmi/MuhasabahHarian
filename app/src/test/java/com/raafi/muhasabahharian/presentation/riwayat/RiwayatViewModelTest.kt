package com.raafi.muhasabahharian.presentation.riwayat

import com.raafi.muhasabahharian.data.local.entity.MuhasabahEntity
import com.raafi.muhasabahharian.data.repository.MuhasabahRepository
import com.raafi.muhasabahharian.presentation.riwayat.RiwayatViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RiwayatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: MuhasabahRepository
    private lateinit var viewModel: RiwayatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `riwayat StateFlow emits mapped RiwayatItem list`() = runTest {
        val sampleEntities = listOf(
            MuhasabahEntity(
                id = 1,
                date = "2025-06-30",
                type = "harian",
                activity = "Meditasi",
                obstacle = "Gangguan",
                note = "Catatan penting",
                mood = "😊",
                score = 8
            )
        )

        coEvery { repository.getAllReflections() } returns flowOf(sampleEntities)

        viewModel = RiwayatViewModel(repository)

        testScheduler.advanceUntilIdle()

        val riwayatList = viewModel.riwayat.value
        assertEquals(1, riwayatList.size)
        val item = riwayatList.first()
        assertEquals("1", item.id)
        assertEquals("harian", item.type)
        assertEquals("Refleksi Harian", item.title)
        assert(item.date.contains("2025"))
        assert(item.description.contains("Meditasi"))
        assert(item.description.contains("Gangguan"))
        assert(item.description.contains("Catatan penting"))
    }
}
