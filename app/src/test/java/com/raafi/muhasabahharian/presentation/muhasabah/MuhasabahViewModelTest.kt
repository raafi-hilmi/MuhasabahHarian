package com.raafi.muhasabahharian.presentation.muhasabah

import com.raafi.muhasabahharian.data.local.entity.MuhasabahEntity
import com.raafi.muhasabahharian.data.repository.MuhasabahRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MuhasabahViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: MuhasabahRepository
    private lateinit var viewModel: MuhasabahViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = MuhasabahViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveReflection calls repository and onSuccess`() = runTest {
        var onSuccessCalled = false

        val answers = mapOf<String, Any>(
            "activity" to "Meditation",
            "obstacle" to "Distraction",
            "note" to "Felt calm",
            "mood" to "😊",
            "score" to 0.8f
        )

        viewModel.saveReflection("Reflection", answers) {
            onSuccessCalled = true
        }

        testScheduler.advanceUntilIdle()

        coVerify {
            repository.insertReflection(match<MuhasabahEntity> {
                it.type == "Reflection" &&
                        it.activity == "Meditation" &&
                        it.obstacle == "Distraction" &&
                        it.note == "Felt calm" &&
                        it.mood == "😊" &&
                        it.score == 8 // 0.8 * 10
            })
        }

        assert(onSuccessCalled)
    }
}
