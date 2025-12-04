package com.example.quranapp.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object SurahList : Screen("surah_list")
    object JuzList : Screen("juz_list")
    object SurahDetail : Screen("surah_detail/{surahNumber}") {
        fun createRoute(surahNumber: Int) = "surah_detail/$surahNumber"
    }
    object MushafPage : Screen("mushaf_page/{page}") {
        fun createRoute(page: Int) = "mushaf_page/$page"
    }
    object AudioPlayer : Screen("audio_player")
    object PrayerTimes : Screen("prayer_times")
    object Qibla : Screen("qibla")
    object Favorites : Screen("favorites")
    object Search : Screen("search")
    object Progress : Screen("progress")
    object Settings : Screen("settings")
    object About : Screen("about")
}

