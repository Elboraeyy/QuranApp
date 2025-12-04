package com.example.quranapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.quranapp.presentation.ui.screens.*

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.SurahList.route) {
            SurahListScreen(navController = navController)
        }
        composable(Screen.JuzList.route) {
            JuzListScreen(navController = navController)
        }
        composable(
            route = Screen.SurahDetail.route,
            arguments = listOf(
                navArgument("surahNumber") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val surahNumber = backStackEntry.arguments?.getInt("surahNumber") ?: 1
            SurahDetailScreen(
                surahNumber = surahNumber,
                navController = navController
            )
        }
        composable(
            route = Screen.MushafPage.route,
            arguments = listOf(
                navArgument("page") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val page = backStackEntry.arguments?.getInt("page") ?: 1
            MushafPageScreen(
                page = page,
                navController = navController
            )
        }
        composable(Screen.AudioPlayer.route) {
            AudioPlayerScreen(navController = navController)
        }
        composable(Screen.PrayerTimes.route) {
            PrayerTimesScreen(navController = navController)
        }
        composable(Screen.Qibla.route) {
            QiblaScreen(navController = navController)
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(navController = navController)
        }
        composable(Screen.Search.route) {
            SearchScreen(navController = navController)
        }
        composable(Screen.Progress.route) {
            ProgressScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.About.route) {
            AboutScreen(navController = navController)
        }
    }
}

