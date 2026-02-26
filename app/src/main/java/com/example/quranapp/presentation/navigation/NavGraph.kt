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
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.TafsirMenu.route) {
            TafsirMenuScreen(navController = navController)
        }
        composable(Screen.TafsirSurahList.route) {
            TafsirSurahListScreen(navController = navController)
        }
        composable(Screen.AdhkarList.route) {
            AdhkarListScreen(navController = navController)
        }
        composable(
            route = Screen.AdhkarDetail.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 1
            AdhkarDetailScreen(
                navController = navController,
                categoryId = categoryId
            )
        }
        composable(Screen.Tasbih.route) {
            TasbihScreen(navController = navController)
        }
        composable(
            route = Screen.TafsirDetail.route,
            arguments = listOf(
                navArgument("surahName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val surahName = backStackEntry.arguments?.getString("surahName") ?: "سُورَةُ الْفَاتِحَةِ"
            TafsirDetailScreen(
                navController = navController,
                surahName = surahName
            )
        }
        composable(Screen.QuranIndex.route) {
            QuranIndexScreen(navController = navController)
        }
        composable(
            route = Screen.QuranReading.route,
            arguments = listOf(
                navArgument("surahId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val surahId = backStackEntry.arguments?.getInt("surahId") ?: 2
            QuranReadingScreen(
                navController = navController,
                surahId = surahId
            )
        }
    }
}

