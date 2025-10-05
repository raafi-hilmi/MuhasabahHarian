package com.raafi.muhasabahharian.presentation.login

import android.app.Activity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.FirebaseUser
import com.raafi.muhasabahharian.R
import com.raafi.muhasabahharian.core.auth.AuthManager
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockActivity = mockk<Activity>(relaxed = true)

    @Before
    fun setup() {
        mockkObject(AuthManager)
        every { AuthManager.getCurrentUser() } returns null
        coEvery { AuthManager.cacheUser(any(), any()) } just Runs
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun loginScreen_displaysAllUIElements() {
        composeTestRule.setContent {
            LoginScreen()
        }

        composeTestRule.onNodeWithContentDescription("Muhasabah Harian Logo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Masuk sebagai Tamu").assertIsDisplayed()
        composeTestRule.onNodeWithText("Masuk dengan Google").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Google Logo").assertIsDisplayed()
    }

    @Test
    fun loginScreen_guestLoginSuccess_callsOnGuestClick() = runTest {
        var guestClickCalled = false
        val mockUser = mockk<FirebaseUser>()

        every { AuthManager.getCurrentUser() } returns mockUser
        coEvery { AuthManager.cacheUser(any(), any()) } just Runs
        every {
            AuthManager.signInAnonymously(
                onSuccess = captureLambda(),
                onFailure = any()
            )
        } answers {
            lambda<() -> Unit>().invoke()
        }

        composeTestRule.setContent {
            LoginScreen(
                onGuestClick = { guestClickCalled = true }
            )
        }


        composeTestRule.onNodeWithText("Masuk sebagai Tamu").performClick()

        composeTestRule.waitForIdle()
        assert(guestClickCalled)
    }

    @Test
    fun loginScreen_guestLoginFailure_showsErrorMessage() = runTest {
        val errorMessage = "Network error"
        val exception = Exception(errorMessage)

        every {
            AuthManager.signInAnonymously(
                onSuccess = any(),
                onFailure = captureLambda()
            )
        } answers {
            lambda<(Exception) -> Unit>().invoke(exception)
        }

        composeTestRule.setContent {
            LoginScreen()
        }
        composeTestRule.onNodeWithText("Masuk sebagai Tamu").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Login tamu gagal: $errorMessage").assertIsDisplayed()
    }

    @Test
    fun loginScreen_googleLoginSuccess_callsOnGoogleSuccess() = runTest {
        var googleSuccessCalled = false
        val mockUser = mockk<FirebaseUser>()

        coEvery { AuthManager.cacheUser(any(), any()) } just Runs
        every {
            AuthManager.signInWithGoogle(
                activity = any(),
                onSuccess = captureLambda(),
                onFailure = any()
            )
        } answers {
            lambda<(FirebaseUser?) -> Unit>().invoke(mockUser)
        }
        composeTestRule.setContent {
            LoginScreen(
                onGoogleSuccess = { googleSuccessCalled = true }
            )
        }
        composeTestRule.onNodeWithText("Masuk dengan Google").performClick()
        composeTestRule.waitForIdle()
        assert(googleSuccessCalled)
    }

    @Test
    fun loginScreen_googleLoginFailure_showsErrorMessage() = runTest {
        val errorMessage = "Google auth failed"
        val exception = Exception(errorMessage)

        every {
            AuthManager.signInWithGoogle(
                activity = any(),
                onSuccess = any(),
                onFailure = captureLambda()
            )
        } answers {
            lambda<(Exception) -> Unit>().invoke(exception)
        }

        composeTestRule.setContent {
            LoginScreen()
        }

        composeTestRule.onNodeWithText("Masuk dengan Google").performClick()

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Login Google gagal: $errorMessage").assertIsDisplayed()
    }

    @Test
    fun loginScreen_googleLoginNullUser_showsErrorMessage() = runTest {
        every {
            AuthManager.signInWithGoogle(
                activity = any(),
                onSuccess = captureLambda(),
                onFailure = any()
            )
        } answers {
            lambda<(FirebaseUser?) -> Unit>().invoke(null)
        }

        composeTestRule.setContent {
            LoginScreen()
        }

        composeTestRule.onNodeWithText("Masuk dengan Google").performClick()

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Autentikasi Google gagal").assertIsDisplayed()
    }

    @Test
    fun loginScreen_layoutStructure() {
        composeTestRule.setContent {
            LoginScreen()
        }

        composeTestRule.onNodeWithContentDescription("Muhasabah Harian Logo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Masuk sebagai Tamu").assertIsDisplayed()
        composeTestRule.onNodeWithText("Masuk dengan Google").assertIsDisplayed()

        composeTestRule.onNode(hasText("Masuk sebagai Tamu").and(hasClickAction())).assertExists()
        composeTestRule.onNode(hasText("Masuk dengan Google").and(hasClickAction())).assertExists()
    }
}