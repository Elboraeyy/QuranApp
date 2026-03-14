package com.example.quranapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.quranapp.presentation.ui.screens.AboutScreen
import com.example.quranapp.presentation.ui.screens.AdhkarDetailScreen
import com.example.quranapp.presentation.ui.screens.AdhkarListScreen
import com.example.quranapp.presentation.ui.screens.AudioPlayerScreen
import com.example.quranapp.presentation.ui.screens.BookmarkedHadithsScreen
import com.example.quranapp.presentation.ui.screens.ReligiousTasksScreen
import com.example.quranapp.presentation.ui.screens.TaskHistoryScreen
import com.example.quranapp.presentation.ui.screens.TaskCustomizationScreen
import com.example.quranapp.presentation.ui.screens.FavoritesScreen
import com.example.quranapp.presentation.ui.screens.HadithDetailScreen
import com.example.quranapp.presentation.ui.screens.HadithListScreen
import com.example.quranapp.presentation.ui.screens.HomeScreen
import com.example.quranapp.presentation.ui.screens.JuzListScreen
import com.example.quranapp.presentation.ui.screens.LoginScreen
import com.example.quranapp.presentation.ui.screens.MushafPageScreen
import com.example.quranapp.presentation.ui.screens.OnboardingScreen
import com.example.quranapp.presentation.ui.screens.PrayerTimesScreen
import com.example.quranapp.presentation.ui.screens.ProgressScreen
import com.example.quranapp.presentation.ui.screens.QiblaScreen
import com.example.quranapp.presentation.ui.screens.QuranIndexScreen
import com.example.quranapp.presentation.ui.screens.QuranReadingScreen
import com.example.quranapp.presentation.ui.screens.SearchScreen
import com.example.quranapp.presentation.ui.screens.SettingsScreen
import com.example.quranapp.presentation.ui.screens.SplashScreen
import com.example.quranapp.presentation.ui.screens.SurahDetailScreen
import com.example.quranapp.presentation.ui.screens.SurahListScreen
import com.example.quranapp.presentation.ui.screens.TafsirDetailScreen
import com.example.quranapp.presentation.ui.screens.TafsirMenuScreen
import com.example.quranapp.presentation.ui.screens.TafsirSurahListScreen
import com.example.quranapp.presentation.ui.screens.TasbihCountingScreen
import com.example.quranapp.presentation.ui.screens.TasbihListScreen

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
        composable(Screen.DailyTasks.route) {
            ReligiousTasksScreen(navController = navController)
        }
        composable(Screen.TaskHistory.route) {
            TaskHistoryScreen(navController = navController)
        }
        composable(Screen.TaskCustomization.route) {
            TaskCustomizationScreen(navController = navController)
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
        composable(Screen.TasbihList.route) {
            TasbihListScreen(navController = navController)
        }
        composable(
            route = Screen.TasbihCounting.route,
            arguments = listOf(
                navArgument("tasbihId") { type = NavType.IntType }
            )
        ) {
            TasbihCountingScreen(navController = navController)
        }
        composable(Screen.HadithList.route) {
            HadithListScreen(navController = navController)
        }
        composable(
            route = Screen.HadithDetail.route,
            arguments = listOf(
                navArgument("hadithId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val hadithId = backStackEntry.arguments?.getInt("hadithId") ?: 1
            HadithDetailScreen(
                navController = navController,
                hadithId = hadithId
            )
        }
        composable(Screen.BookmarkedHadiths.route) {
            BookmarkedHadithsScreen(navController = navController)
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
                navArgument("startPage") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val startPage = backStackEntry.arguments?.getInt("startPage") ?: 1
            QuranReadingScreen(
                navController = navController,
                startPage = startPage
            )
        }
    }
}

