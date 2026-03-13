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
    object DailyTasks : Screen("daily_tasks")
    object Qibla : Screen("qibla")
    object Favorites : Screen("favorites")
    object Search : Screen("search")
    object Progress : Screen("progress")
    object Settings : Screen("settings")
    object About : Screen("about")
    object Login : Screen("login")
    object Notifications : Screen("notifications")
    object TafsirMenu : Screen("tafsir_menu")
    object TafsirSurahList : Screen("tafsir_surah_list")
    object AdhkarList : Screen("adhkar_list")
    object AdhkarDetail : Screen("adhkar_detail/{categoryId}") {
        fun createRoute(categoryId: Int) = "adhkar_detail/$categoryId"
    }
    object TasbihList : Screen("tasbih_list")
    object TasbihCounting : Screen("tasbih_counting/{tasbihId}") {
        fun createRoute(tasbihId: Int) = "tasbih_counting/$tasbihId"
    }
    object HadithList : Screen("hadith_list")
    object HadithDetail : Screen("hadith_detail/{hadithId}") {
        fun createRoute(hadithId: Int) = "hadith_detail/$hadithId"
    }
    object QuranIndex : Screen("quran_index")
    object QuranReading : Screen("quran_reading/{startPage}") {
        fun createRoute(startPage: Int) = "quran_reading/$startPage"
    }
    object TafsirDetail : Screen("tafsir_detail/{surahName}") {
        fun createRoute(surahName: String) = "tafsir_detail/$surahName"
    }
    object BookmarkedHadiths : Screen("bookmarked_hadiths")
}

