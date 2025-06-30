package com.raafi.muhasabahharian

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.raafi.muhasabahharian.core.auth.sync.SyncManager
import com.raafi.muhasabahharian.core.navigation.NavigationGraph
import com.raafi.muhasabahharian.core.navigation.Routes
import com.raafi.muhasabahharian.presentation.login.LoginScreen
import com.raafi.muhasabahharian.core.theme.MuhasabahHarianTheme
import com.raafi.muhasabahharian.core.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()
        actionBar?.hide()
        setContent {
            MuhasabahHarianTheme {
                Surface(
                    modifier = Modifier.fillMaxSize().systemBarsPadding(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    hiltViewModel<SyncManager>()

                    val navController = rememberNavController();
                    val startRoute = remember {
                        if (SessionManager.isUserSignedIn()) Routes.HOME else Routes.LOGIN
                    }

                    NavigationGraph(
                        navController = navController,
                        startDestination = startRoute
                    )
                }
            }
        }
    }
}
