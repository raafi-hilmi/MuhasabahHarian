package com.raafi.muhasabahharian.core.navigation

import ChooseTemplateScreen
import MuhasabahScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.raafi.muhasabahharian.core.auth.AuthManager
import com.raafi.muhasabahharian.presentation.login.LoginScreen
import com.raafi.muhasabahharian.presentation.home.HomeScreen
import com.raafi.muhasabahharian.presentation.profile.ProfileScreen
import com.raafi.muhasabahharian.presentation.riwayat.DetailRiwayatScreen
import com.raafi.muhasabahharian.presentation.riwayat.RiwayatScreen
import com.raafi.muhasabahharian.presentation.setting.SettingScreen
import com.raafi.muhasabahharian.presentation.statistik.StatistikScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    startDestination: String = Routes.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onGuestClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(0)
                    }
                },
                onGoogleSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToMuhasabah = {
                    navController.navigate(Routes.CHOOSE_TEMPLATE)
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                },
                onNavigateToStatistics = {
                    navController.navigate(Routes.STATISTIK)
                },
                onNavigateToRiwayat = {
                    navController.navigate(Routes.RIWAYAT)
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTING)
                }
            )
        }
        composable(Routes.CHOOSE_TEMPLATE) {
            // Pastikan Anda mengimpor ChooseTemplateScreen dari package yang benar
            ChooseTemplateScreen(navController = navController)
        }
        composable(
            route = "muhasabah_screen/{mood}", // Tambahkan {mood} untuk menangkap argumen
            arguments = listOf(navArgument("mood") { type = NavType.StringType }) // Definisikan tipe argumen
        ) { backStackEntry ->
            // Ambil argumen yang dikirim
            val moodName = backStackEntry.arguments?.getString("mood")

            if (moodName != null) {
                // Panggil MuhasabahScreen dengan argumen moodName
                MuhasabahScreen(navController = navController, moodName = moodName)
            } else {
                // Handle jika moodName null, misal kembali ke halaman sebelumnya
                navController.popBackStack()
            }
        }
//
//        composable("${Routes.MUHASABAH}/{type}") { backStackEntry ->
//            val type = backStackEntry.arguments?.getString("type") ?: "produktif"
//            MuhasabahScreen(
//                type = type,
//                onNavigateBack = { navController.popBackStack() },
//                onNavigateToHome = {
//                    navController.navigate(Routes.HOME)
//                }
//
//            )
//        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSetting = { navController.navigate(Routes.SETTING) }
            )
        }

        composable(Routes.STATISTIK) {
            StatistikScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRiwayat = { navController.navigate(Routes.RIWAYAT) },
                onNavigateToMuhasabah = { navController.navigate(Routes.CHOOSE_TEMPLATE) },
                onNavigateToHome = { navController.navigate(Routes.HOME) }
            )
        }

        composable(Routes.RIWAYAT) {
            RiwayatScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { riwayatId ->
                    navController.navigate("${Routes.DETAIL_RIWAYAT}/$riwayatId")
                }
            )
        }

        composable("${Routes.DETAIL_RIWAYAT}/{riwayatId}") { backStackEntry ->
            val riwayatId = backStackEntry.arguments?.getString("riwayatId") ?: ""
            DetailRiwayatScreen(
                riwayatId = riwayatId,
                onNavigateBack = { navController.popBackStack() },
                onEditRiwayat = { id ->
                },
                onDeleteRiwayat = { id ->
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.SETTING) {
            SettingScreen(
                onNavigateBack = { navController.popBackStack() },
                isLoggedIn     = AuthManager.getCurrentUser()?.isAnonymous == false,
                userEmail      = AuthManager.getCurrentUser()?.email ?: "",
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val CHOOSE_TEMPLATE = "choose_template"
    const val MUHASABAH = "muhasabah"
    const val PROFILE = "profile"
    const val STATISTIK = "statistik"
    const val RIWAYAT = "riwayat"
    const val DETAIL_RIWAYAT = "detail_riwayat"
    const val SETTING = "setting"
}